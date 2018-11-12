package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.SystemParameters;
import com.war4.enums.*;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.Coupon;
import com.war4.pojo.FilmOrder;
import com.war4.pojo.UserCorrelative;
import com.war4.pojo.UserCoupon;
import com.war4.service.*;
import com.war4.util.*;
import com.war4.vo.PushMsgVO;
import com.war4.vo.film.FilmOrderVO;
import com.war4.vo.film.YingCinemaConfigVO;
import com.war4.vo.film.YingCinemaPlayVO;
import com.war4.vo.film.YingSeatLockVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2017/1/12.
 */
@Service
@Slf4j
public class FilmOrderServiceImpl extends BaseService implements FilmOrderService {

    //定时器缓存
    private static Map<String,Timer> orderTimerCache = new HashMap<>();

    //清除定时器
    private void delOrderTimer(String orderId){
        Timer timer = orderTimerCache.get(orderId);
        if (timer != null){
            timer.cancel();
        }
        orderTimerCache.remove(orderId);
    }

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private UserCouponService userCouponService;
    @Autowired
    private BaseOrderService baseOrderService;
    @Autowired
    private KouDianYingOrderService kouDianYingOrderService;
    @Autowired
    private UserCorrelativeService correlativeService;
    @Autowired
    private YinghezhongService yinghezhongService;

    @Override
    @Transactional
    public void setOrderPaySuccess(String orderId) throws Exception {
        FilmOrder filmOrder = baseRepository.getObjById(FilmOrder.class, orderId);
        if (filmOrder != null && FilmOrderStatus.CREATE.getCode().equals(filmOrder.getStatus())) {
            filmOrder.setStatus(FilmOrderStatus.PAY_SUCCESS.getCode());
            filmOrder.setPayTime(new Date());
            //判断订单类型
            if (orderId.startsWith(OrderUtil.KOU_DIAN_YING)) {//抠电影
                kouDianYingOrderService.confirmMovieOrder(orderId);
            }else if (orderId.startsWith(OrderUtil.TIAN_ZHI)){
                //天智创客
                yinghezhongService.confirmMovieOrder(orderId);
            }
            //用户电影订单+1
            correlativeService.addCount(UserCorrelative.FILM_ORDERS,filmOrder.getFkUserId());
            //推送信息
            PushMsgVO pmv = new PushMsgVO(filmOrder.getFkUserId(),
                    null,SystemParameters.MESSAGEFILM_ORDER_PAY_SUCCESS,
                    SystemParameters.MESSAGEFILM_ORDER_PAY_SUCCESS + "。订单号:" + orderId,
                    orderId,MessageLogType.FILM_ORDER,
                    true);
            ac.publishEvent(new PushEvent(pmv));
            baseRepository.updateObj(filmOrder);
        }
    }

    @Override
    @Transactional
    public FilmOrder queryFilmOrderById(String orderId) throws Exception {
        FilmOrder filmOrder = baseRepository.getObjById(FilmOrder.class, orderId);
        if (filmOrder != null) {
            //判断订单时间
            if (filmOrder.getStatus().equals(FilmOrderStatus.CREATE.getCode()) && DateUtil.intervalTime(filmOrder.getCreateTime(),new Date(),DateUtil.MINUTE) >= 15) {
                //取消订单
                this.cancelCoupon(filmOrder);
                baseRepository.updateObj(filmOrder);
            }
            filmOrder = assembleService.assembleObject(filmOrder);
        }
        return filmOrder;
    }

    @Override
    public CutPage<FilmOrder> queryMyOrderList(String fkUserId, Integer status, Integer page, Integer limit) throws Exception {
        StringBuilder sb = new StringBuilder(200);
        sb.append("from FilmOrder where delFlag = 0 and fkUserId = :userId ");
        Map<String,Object> param = new HashMap<>();
        param.put("userId",fkUserId);
        if (status != null) {
            if (FilmOrderStatus.getByCode(status) == null) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不存在的状态");
            }
            if (FilmOrderStatus.WAIT_EVALUATE.getCode().equals(status)){
                sb.append(" and status = 4 and (timesTampDiff(MINUTE,startTime,NOW()) >= 120 or startTime = null)");
            }else if (FilmOrderStatus.SUCCESS.getCode().equals(status)){
                sb.append(" and status = 4 and timesTampDiff(MINUTE,startTime,NOW()) <= 120");
            }else {
                sb.append(" and status = :status");
                param.put("status",status);
            }
        }
        sb.append(" order by createTime desc");
        CutPage<FilmOrder> cutPage = baseRepository.executeHql(sb.toString(), param, page, limit);
        for (FilmOrder filmOrder : cutPage.getiData()) {
            assembleService.assembleObject(filmOrder);
            if (StringUtils.isNotBlank(filmOrder.getFkUserCouponId())){
                filmOrder.setUserCoupon(userCouponService.queryUserCouponById(filmOrder.getFkUserCouponId()));
            }
        }
        return cutPage;
    }

    @Override
    @Transactional
    public void disposeOverdueOrder(Integer minute) {
        String hql = "from FilmOrder where delFlag = 0 and status = :status and timesTampDiff(MINUTE,createTime,NOW()) >= :minute";
        Map<String,Object> map = new HashMap<>();
        map.put("status",FilmOrderStatus.CREATE.getCode());
        map.put("minute",minute);
        CutPage<FilmOrder> cutPage = baseRepository.executeHql(hql, map, 0, CutPage.MAX_COUNT);
        for (FilmOrder filmOrder : cutPage.getiData()) {
            if (filmOrder.getFkUserCouponId() != null) {
                this.cancelCoupon(filmOrder);
            }
            filmOrder.setStatus(FilmOrderStatus.CANCEL.getCode());
            baseRepository.updateObj(filmOrder);
        }
    }

    @Override
    public CutPage<FilmOrder> queryAllOrderList(String src, Integer payType, String status, String keyword, Integer page, Integer limit){
        StringBuilder hql = new StringBuilder(500);
        Map<String, Object> paramMap = baseRepository.paramMap();
        hql.append("from FilmOrder where delFlag = 0 and status in :status ");
        if (StringUtils.equalsIgnoreCase(status,"paySuccess")) {
            paramMap.put("status", Arrays.asList(FilmOrderStatus.COMPLETE.getCode(),FilmOrderStatus.PAY_SUCCESS.getCode(),
                    FilmOrderStatus.SUCCESS.getCode(),FilmOrderStatus.WAIT_EVALUATE.getCode()));
        }else if (StringUtils.equalsIgnoreCase(status,"cancel")){
            paramMap.put("status", FilmOrderStatus.CANCEL.getCode());
        }else if (StringUtils.equalsIgnoreCase(status,"create")){
            paramMap.put("status", FilmOrderStatus.CREATE.getCode());
        }else {
            paramMap.put("status", FilmOrderStatus.FAILED.getCode());
        }
        if (StringUtils.isNoneBlank(keyword)) {
            hql.append(" and (fkOrderId like :keyword or fkUserId like :keyword or fkUserCouponId like :keyword or outTradeNo like :keyword or mobile like :keyword) ");
            paramMap.put("keyword","%" + keyword + "%");
        }
        if (payType != null) {
            OrderPayType type = OrderPayType.getByCode(payType);
            if (type == null) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"支付类型不存在");
            }
            hql.append(" and payType = :payType ");
            paramMap.put("payType",type.getValue());
        }
        if (StringUtils.isNoneBlank(src)){
            hql.append(" and fkOrderId like :src ");
            paramMap.put("src",src + "%");
        }
        hql.append(" order by createTime desc");
        return baseRepository.executeHql(hql,paramMap,page,limit);
    }

    @Override
    @Transactional
    public FilmOrder createFilmOrder(Long planId, String seatNo, String mobile, String userId) throws Exception {
        if (planId == null || StringUtils.isAnyBlank(seatNo,mobile,userId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "参数信息不完整！");
        }
        int maxNum = 4;
        //限制购买数量
        if (seatNo.split(",").length > maxNum) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "亲，最多不能超过" + maxNum + "个座位哦~");
        }
        //创建抠电影订单，获取订单号
        FilmOrderVO order = kouDianYingOrderService.addOrder(planId.toString(), seatNo, false, mobile);
        BigDecimal money = new BigDecimal(order.getMoney());
        //创建本地订单
        FilmOrder filmOrder = new FilmOrder();
        filmOrder.setFkOrderId(order.getOrderId());
        filmOrder.setFkUserId(userId);
        filmOrder.setCount(order.getCount());
        filmOrder.setUnitPrice(money.divide(new BigDecimal(filmOrder.getCount()), RoundingMode.HALF_UP));
        filmOrder.setMobile(mobile);
        filmOrder.setMoney(money);
        filmOrder.setPayMoney(money);
        filmOrder.setStatus(FilmOrderStatus.CREATE.getCode());
        baseRepository.saveObj(filmOrder);
        //通用订单
        baseOrderService.addBaseOrder(filmOrder.getFkOrderId(),OrderType.FILM_ORDER,userId);
        //保存订单数据到mongodb
        order.setUserId(filmOrder.getFkUserId());
        template.insert(order);
        //TODO ===================== 订单时间超过十五分钟后自动取消 =========================
        //创建倒计时
        orderTimer(filmOrder.getFkOrderId());
        //=============================================
        //返回订单号
        return filmOrder;
    }

    @Override
    @Transactional
    public FilmOrder updateFilmOrder(FilmOrder filmOrder) throws Exception {
        //根据订单号获取订单
        FilmOrder fo = baseRepository.getObjById(FilmOrder.class, filmOrder.getFkOrderId());
        //判断是否为已创建状态
        if (fo != null && FilmOrderStatus.CREATE.getCode().equals(fo.getStatus())) {
            //初始化订单和优惠券状态
            if (fo.getFkUserCouponId() != null) {
                this.cancelCoupon(fo);
            }
            String userCouponId = filmOrder.getFkUserCouponId();
            if (StringUtils.isNoneBlank(userCouponId)) {
                //判断用户是否拥有该优惠券（根据优惠券id和用户id查询）
                UserCoupon userCoupon = userCouponService.queryUserCouponByUcIdAndUsId(userCouponId, fo.getFkUserId());
                if (userCoupon != null) {
                    //判断优惠券是否已使用、是否在有效期内
                    if (UserCouponStatus.USED.getCode().equals(userCoupon.getIsUse()) || new Date().after(userCoupon.getOffTime())) {
                        throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"优惠券已失效！");
                    }
                    Coupon coupon = userCoupon.getCoupon();
                    //如果优惠券有电影id时，说明需要指定电影，目前只有抠电影可以限制电影，如果不是抠电影订单，不处理电影
                    //特定类型：需要指定电影
                    if (StringUtils.isNoneBlank(coupon.getMovieId()) && fo.getFkOrderId().contains(OrderUtil.KOU_DIAN_YING)) {
                        //查询订单的电影和优惠券的电影是否相同
                        FilmOrderVO vo = kouDianYingOrderService.queryOrder(filmOrder.getFkOrderId());
                        if (Long.parseLong(coupon.getMovieId()) != (vo.getPlan().getMovieId())) {
                            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不符合优惠券的使用要求");
                        }
                    }
                    //普通类型：订单总金额 - 优惠券金额
                    //通用类型：订单总金额 - 优惠券座位数 * 座位单价
                    BigDecimal payMoney = fo.getMoney().subtract(FilmOrderPriceUtil.countFilmOrderReducePrice(fo,coupon));
                    if (payMoney.compareTo(BigDecimal.ZERO) <= 0){
                        payMoney = BigDecimal.ZERO;
                        fo.setPayType(OrderPayType.COUPON.getValue());
                        fo.setOutTradeNo(null);
                        this.setOrderPaySuccess(fo.getFkOrderId());
                    }
                    fo.setPayMoney(payMoney);
                    fo.setReducePrice(fo.getMoney().subtract(fo.getPayMoney()));
                    //在订单中添加优惠券信息
                    fo.setFkUserCouponId(userCouponId);
                    //将优惠券设置为已使用状态
                    userCouponService.updateUserCouponUsed(userCouponId);
                }
            }
            //设置手机号码
            fo.setMobile(filmOrder.getMobile());
            baseRepository.updateObj(fo);
        }else throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"订单超时，请重新下单！");
        return fo;
    }

    private FilmOrder cancelCoupon(FilmOrder filmOrder){
        //原来有使用优惠券，恢复成未使用状态
        UserCoupon userCoupon = userCouponService.queryUserCouponById(filmOrder.getFkUserCouponId());
        if (userCoupon != null) {
            BigDecimal payMoney = filmOrder.getPayMoney().add(FilmOrderPriceUtil.countFilmOrderReducePrice(filmOrder,userCoupon.getCoupon()));
            //加上原来优惠的金额,如果相加后大于影片原价，则设置为影片原价
            filmOrder.setPayMoney(payMoney.compareTo(filmOrder.getMoney()) > 0 ? filmOrder.getMoney() : payMoney);
            userCouponService.updateUserCouponNotUse(filmOrder.getFkUserCouponId());//把优惠券设置为未使用状态
        }
        filmOrder.setFkUserCouponId(null);
        return filmOrder;
    }

    @Override
    @Transactional
    public void deleteFilmOrder(String orderId, String fkUserId) throws Exception {
        //根据orderId和fkUserId获取本地的订单
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("orderId",orderId);
        paramMap.put("userId",fkUserId);
        String hql = "from FilmOrder where delFlag = 0 and fkOrderId = :orderId and fkUserId = :userId";
        FilmOrder filmOrder = baseRepository.executeHql(hql, paramMap);
        //判断本地订单是否存在和是否为创建状态
        if (filmOrder != null && FilmOrderStatus.CREATE.getCode().equals(filmOrder.getStatus())) {
            //将订单状态改为CANCEL状态和删除状态
            filmOrder.setStatus(FilmOrderStatus.CANCEL.getCode());
            //将订单中的优惠券设置为未使用状态
            if (filmOrder.getFkUserCouponId() != null) {
                userCouponService.updateUserCouponNotUse(filmOrder.getFkUserCouponId());
                filmOrder.setFkUserCouponId(null);//清除优惠券
            }
            baseRepository.logicDelete(filmOrder);
            //删除第三方中的订单
            //判断订单类型
            if (orderId.contains("a")) {
                kouDianYingOrderService.deleteOrder(orderId);
            }else if (orderId.contains(OrderUtil.TIAN_ZHI)){
                yinghezhongService.seatUnlock(orderId);
            }
            try {
                yinghezhongService.updateFilmOrderVO(orderId, FilmOrderStatus.CANCEL.getCode());//本地订单取消状态
            }catch (Exception e){
                e.printStackTrace();
                log.warn("mongodb电影订单取消状态更改失败 orderId {}，原因 {}" ,orderId,e.getMessage());
            }
            //删除缓存中的定时器
            delOrderTimer(orderId);
        }
    }

    @Override
    @Transactional
    public void cancelFilmOrder(String orderId) throws Exception {
        //获取订单信息
        FilmOrder filmOrder = baseRepository.getObjById(FilmOrder.class, orderId);
        //判断是否为已创建状态
        if (filmOrder != null && FilmOrderStatus.CREATE.getCode().equals(filmOrder.getStatus())) {
            //将订单状态改为CANCEL状态和删除状态
            filmOrder.setStatus(FilmOrderStatus.CANCEL.getCode());
            //filmOrder.setDelFlag(CommonDeleteFlag.DELETED.getCode());
            //将订单中的优惠券设置为未使用状态
            if (filmOrder.getFkUserCouponId() != null) {
                userCouponService.updateUserCouponNotUse(filmOrder.getFkUserCouponId());
                filmOrder.setFkUserCouponId(null);//清除优惠券
            }
            baseRepository.updateObj(filmOrder);
            yinghezhongService.updateFilmOrderVO(orderId,FilmOrderStatus.CANCEL.getCode());//本地订单取消状态
            System.out.println("=============订单删除=============");
        }
    }

    @Override
    @Transactional
    public FilmOrder createYingFilmOrder(String cinemaId, Long planId, String seatNo, String mobile, String userId) throws Exception {
        if (planId == null || StringUtils.isAnyBlank(seatNo,mobile,userId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "参数信息不完整！");
        }
        YingCinemaConfigVO cinemaConfig = yinghezhongService.getCinemaConfig(cinemaId);
        if (cinemaConfig == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "影院不存在！");
        }
        int maxNum = cinemaConfig.getBuyNumLimit();
        //限制购买数量
        int count = seatNo.split(",").length;
        if (count > maxNum) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "亲，最多不能超过" + maxNum + "个座位哦~");
        }
        YingCinemaPlayVO playInfo = yinghezhongService.getPlayInfo(cinemaId, planId);
        if (playInfo == null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "场次信息错误！");
        }
        //锁定座位
        YingSeatLockVO seatLock = YingEntitytUtil.seatLock(cinemaId, planId.toString(), playInfo.getCineUpdateTime(), seatNo);

        //创建本地订单
        FilmOrder filmOrder = new FilmOrder();
        String orderId = OrderUtil.TIAN_ZHI + OrderUtil.getUUID();
        filmOrder.setFkOrderId(orderId);
        filmOrder.setFkUserId(userId);
        filmOrder.setCount(count);
        filmOrder.setMobile(mobile);
        filmOrder.setStatus(FilmOrderStatus.CREATE.getCode());
        //设置订单金额
        FilmOrderPriceUtil.countFilmOrderMoney(filmOrder,playInfo,seatNo,seatLock);
        baseRepository.saveObj(filmOrder);
        //通用订单
        baseOrderService.addBaseOrder(filmOrder.getFkOrderId(),OrderType.FILM_ORDER,userId);
        //添加订单详细信息
        yinghezhongService.saveFilmOrderAndHistory(cinemaId,planId,seatNo,filmOrder,seatLock);
        //创建定时器
        orderTimer(orderId);
        return filmOrder;
    }

    /**
     * 订单定时器
     * @param orderId
     */
    private void orderTimer(String orderId){
        //创建倒计时
        Timer timer = new Timer(orderId);
        //将定时器放到缓存中
        orderTimerCache.put(orderId,timer);
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            //异步处理
            FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
                @Transactional
                public String call() throws Exception { //建议抛出异常
                    // 15分钟后执行
                    timer.schedule(new TimerTaskForUser() {
                        @Transactional
                        public void run() {
                            try {
                                cancelFilmOrder(orderId);
                                timer.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000 * 60 * 15);
                    return "YES";
                }
            });
            executor.execute(future);
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            //线程池必须关闭
            executor.shutdown();
        }
    }
}

