package com.war4.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.enums.*;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.*;
import com.war4.service.*;
import com.war4.util.*;
import com.war4.vo.AuditParamVO;
import com.war4.vo.PushMsgVO;
import com.war4.vo.film.CinemaVO;
import com.war4.vo.film.FilmOrderVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hh on 2017.10.30 0030.
 */
@Service
public class AuctionServiceImpl extends BaseService implements AuctionService {
    @Autowired
    private FileService fileService;
    @Autowired
    private AssembleService assembleService;
    @Autowired
    private BaseOrderService baseOrderService;
    @Autowired
    private UserCorrelativeService correlativeService;
    @Autowired
    private TaskListService taskListService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private MessageCenterService messageCenterService;
    @Autowired
    private UserRatioService userRatioService;
    @Autowired
    private UserInfoBaseService userInfoBaseService;
    @Autowired
    private FilmOrderService filmOrderService;

    @Override
    @Transactional
    public void applyAuction(MultipartFile[] files, Auction auction) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, auction.getApplierId());
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在！");
        }
        if (user.getHasAuctionProcess()) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您已有一份竞拍在处理！");
        }
        //是否为内部会员
        boolean isInternalUser = user.getInternalFlag().equals(1);
        //判断是否红人
        if (!isInternalUser && !user.getHasInternetStarPass() && !user.getHasCelebrityPass()) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您还没通过红人申请！");
        }
        if (auction.getAuctionPrice() == null || auction.getFinalTime() == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "参数缺失");
        }
        AuctionInternetStarType type;
        if (auction.getType() != null) {
            type = AuctionInternetStarType.getByCode(auction.getType());
        } else {
            type = user.getHasInternetStarPass() ? AuctionInternetStarType.INTERNET_STAR : AuctionInternetStarType.CELEBRITY;
        }
        this.disposeAuctionDetail(files, auction, type, isInternalUser);
        baseRepository.saveObj(auction);
        //设置为有竞拍状态
        user.addState(BitStateUtil.OP_HAS_AUCTION_PROCESS);
        baseRepository.updateObj(user);
    }

    private Auction disposeAuctionDetail(MultipartFile[] files, Auction auction, AuctionInternetStarType type, boolean isInternalUser) {
        //如果订单是发起方支付，则检查订单购买状态
        if (CommonPaySite.CREATOR.getCode().equals(auction.getPaySite())) {
            try {
                FilmOrder filmOrder = filmOrderService.queryFilmOrderById(auction.getFilmOrderId());
                if (filmOrder == null) {
                    throw new RuntimeException("电影订单不存在");
                }
                switch (filmOrder.getStatus()) {
                    case 1:
                    case 2:
                    case 5:
                    case 6:
                        throw new RuntimeException("电影订单不存在");
                }
                //判断观看电影时间是否在有效范围：
                //startTime >= currentTime + lowestPeriod + intervalTime - 1
                //finishTime <= startTime - intervalTime
                //当前到开始时间
                Integer current2Start = DateUtil.intervalTime(filmOrder.getStartTime(), new Date(), DateUtil.HOUR);
                if (new Date().after(filmOrder.getStartTime()) ||
                        //new Date().after(auction.getFinalTime()) || auction.getFinalTime().after(filmOrder.getStartTime()) ||
                        current2Start <= PropertiesConfig.getAuctionLowestPeriod() + PropertiesConfig.getIntervalAuctionFinishWithFilmStartTime() - 1) {
                    throw new RuntimeException("电影订单不在有效期内");
                }
                //设置竞拍结束时间：观看时间 - intervalTime
                auction.setFinalTime(DateUtils.addHours(filmOrder.getStartTime(), -PropertiesConfig.getIntervalAuctionFinishWithFilmStartTime()));
                /*Integer final2Start = DateUtil.intervalTime(auction.getFinalTime(), filmOrder.getStartTime(),DateUtil.HOUR);
                if (final2Start <= PropertiesConifig.getIntervalAuctionFinishWithFilmStartTime()) {
                    throw new RuntimeException("竞拍结束时间与电影票时间至少相隔" + PropertiesConifig.getIntervalAuctionFinishWithFilmStartTime() + "小时");
                }*/
                if (filmOrder.getCount() < 2) {
                    throw new RuntimeException("电影票座位数必须大于2人");
                }
                //设置地址
                FilmOrderVO vo = filmOrder.getJsonArray().getObject(0, FilmOrderVO.class);
                if (vo != null) {
                    CinemaVO cinema = vo.getPlan().getCinema();
                    auction.setLat(cinema.getLatitude());
                    auction.setLng(cinema.getLongitude());
                    auction.setMovieName(vo.getPlan().getMovie().getMovieName());
                    auction.setSite(cinema.getCityName() + "-" + cinema.getDistrictName() + "," + cinema.getCinemaName());
                    auction.setPlanTime(vo.getPlan().getFeatureTimeDate());
                    auction.setMovieImg(vo.getPlan().getMovie().getPathVerticalS());
                    if (vo.getOrderId().startsWith(OrderUtil.KOU_DIAN_YING)) {//抠电影订单
                        auction.setCinemaId(KouDianYingServiceImpl.KOU_CINEMA_ID_PREFIX + cinema.getCinemaId());
                    }
                }
            } catch (Exception e) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, e.getLocalizedMessage());
            }
        } else if (StringUtils.isNoneBlank(auction.getCinemaId())) {
            //查询影院信息
            CinemaVO cinema = template.findOne(new Query(Criteria.where("cinemaId").is(auction.getCinemaId())), CinemaVO.class);
            if (cinema != null) {
                auction.setSite(cinema.getCityName() + "-" + cinema.getDistrictName() + "," + cinema.getCinemaName());
                auction.setLat(cinema.getLatitude());
                auction.setLng(cinema.getLongitude());
            }
        }
        auction.setType(type.getCode());
        auction.setBidPrice(auction.getAuctionPrice());
        //处理图片
        try {
            List<String> list = fileService.saveFile(FileTypeAndPath.IMAGE_TYPE_PATH,FilePurpose.AUCTION_ALBUM, auction.getApplierId(), files);
            auction.setAlbum(JSONArray.toJSONString(list));
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "图片上传异常，请联系客服！");
        }
        if (isInternalUser) {
            auction.setStatus(AuditStatus.PASS.getCode());
            auction.setAuditorId(UserContext.getUserId());
            auction.setRemark("内部会员直接通过");
        } else {
            auction.setStatus(AuditStatus.CREATE.getCode());
        }
        return auction;
    }

    @Override
    public void editAuction(MultipartFile[] files, Auction auction) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, UserContext.getUserId());
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在！");
        }
        //是否为内部会员
        boolean isInternalUser = user.getInternalFlag().equals(1);
        Auction a = this.getProceedAuctionByUser(user.getId());
        if (a != null) {
            if (a.getBidCount() > 0) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "该竞拍已有人参与，不可编辑");
            }
            //修改编辑内容
            AuctionInternetStarType type = user.getHasInternetStarPass() ? AuctionInternetStarType.INTERNET_STAR : AuctionInternetStarType.CELEBRITY;
            a.setCinemaId(auction.getCinemaId());
            a.setFinalTime(auction.getFinalTime());
            a.setAuctionPrice(auction.getAuctionPrice());
            a.setFilmOrderId(auction.getFilmOrderId());
            a.setPaySite(auction.getPaySite());
            this.disposeAuctionDetail(files, a, type, isInternalUser);
            baseRepository.updateObj(a);
        }
    }

    @Override
    public Auction getProceedAuctionByUser(String userId) {
        String hql = "from Auction where status = :status and applierId = :userId ";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("status", AuditStatus.PASS.getCode());
        paramMap.put("userId", userId);
        return baseRepository.executeHql(hql, paramMap);
    }

    @Override
    public CutPage<Auction> queryAllAuction(Integer status, Integer type, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(200);
        hql.append("from Auction where 1 = 1 ");
        Map<String, Object> map = new HashMap<>();
        if (type != null) {
            hql.append(" and type = :type");
            map.put("type", type);
        }
        if (status != null) {
            switch (status) {
                case 0://未审核
                    hql.append(" and status = :status order by createTime desc");
                    map.put("status", AuditStatus.CREATE.getCode());
                    break;
                case 1://除未审核和拒绝外
                    hql.append(" and status not in (:status) order by finalTime desc,createTime desc");
                    map.put("status", Arrays.asList(AuditStatus.CREATE.getCode(), AuditStatus.REFUSE.getCode(), AuditStatus.AWAIT.getCode()));
                    break;
                case 2://通过（进行中）
                    hql.append(" and status = :status");
                    map.put("status", AuditStatus.PASS.getCode());
                    break;
                case 4://拒绝
                    hql.append(" and status = :status");
                    map.put("status", AuditStatus.REFUSE.getCode());
                    break;
                case 5://已结束
                    hql.append(" and status in :status order by auditTime desc");
                    map.put("status", Arrays.asList(AuctionStatus.CANCEL.getCode(), AuctionStatus.FINISH.getCode()));
                    break;
                case 6://已评价
                    hql.append(" and status = :status order by auditTime desc");
                    map.put("status", AuctionStatus.EVALUATE.getCode());
                    break;
            }
        } else {
            hql.append(" order by createTime desc");
        }
        CutPage<Auction> cutPage = baseRepository.executeHql(hql, map, page, limit);
        //设置用户
        this.setAuctionUser(cutPage.getiData());
        return cutPage;
    }

    //注入user对象
    private void setAuctionUser(List<Auction> auctions) {
        List<String> userIds = auctions.stream().map(Auction::getApplierId).collect(Collectors.toList());
        Map<String, UserInfoBase> userMap = userInfoBaseService.queryUserMap(userIds);
        auctions.forEach(a -> a.setUser(userMap.get(a.getApplierId())));
    }

    @Override
    public void updateAuction(Auction auction) {
        Auction a = baseRepository.getObjById(Auction.class, auction.getId());
        if (a == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该竞拍!");
        }
        if (auction.getViewCount() != null) {
            a.setViewCount(auction.getViewCount());
        }
        if (auction.getFinalTime() != null) {
            if (!AuditStatus.PASS.getCode().equals(a.getStatus()) || !AuditStatus.CREATE.getCode().equals(a.getStatus())) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单已无法修改时间");
            }
            a.setFinalTime(auction.getFinalTime());
        }
        /*if (StringUtils.isNoneBlank(auction.getContent())) {
            a.setContent(auction.getContent());
        }
        if (auction.getDuration() != null) {
            if (auction.getDuration() <= 0) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "约会时长必须大于0");
            }
            a.setDuration(auction.getDuration());
        }
        if (auction.getDeposit() != null) {
            a.setDeposit(auction.getDeposit());
        }
        */
        if (auction.getAuctionPrice() != null) {
            if (a.getBidCount() > 0) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "已有人竞拍，无法修改起拍价！");
            }
            if (auction.getAuctionPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "起拍价不能为负数");
            }
            a.setAuctionPrice(auction.getAuctionPrice());
            a.setBidPrice(auction.getAuctionPrice());
        }
        baseRepository.updateObj(a);
    }

    @Override
    @Transactional
    public void updateAuctionState(AuditParamVO vo) {
        Auction auction = baseRepository.getObjById(Auction.class, vo.getId());
        if (auction == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该竞拍！");
        }
        if (!AuditStatus.CREATE.getCode().equals(auction.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "该竞拍已经处理！");
        }
        auction.setAuditorId(vo.getAuditorId());
        auction.setAuditTime(new Date());
        auction.setRemark(vo.getRemark());
        auction.setStatus(vo.isPass() ? AuditStatus.PASS.getCode() : AuditStatus.REFUSE.getCode());
        baseRepository.updateObj(auction);
        PushMsgVO pmv;
        if (vo.isPass()) {
            if (auction.getFinalTime().before(new Date())) {//超过最后竞拍时间
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "竞拍已过期，请使用拒绝操作！");
            }
            pmv = new PushMsgVO(auction.getApplierId(),
                    null, "你发起的竞拍已通过审核，已开始竞拍。", "用户Id：" + auction.getApplierId() + "申请竞拍:" + auction.getId() + "通过",
                    vo.getId(), MessageLogType.AUCTION, true);
            pmv.addExtras("skipType", "1");
            pmv.addExtras("auctionId", auction.getId().toString());
        } else {
            //处理用户
            UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, auction.getApplierId());
            if (user != null) {
                user.removeState(BitStateUtil.OP_HAS_AUCTION_PROCESS);
                baseRepository.updateObj(user);
            }
            //推送信息
            pmv = new PushMsgVO(auction.getApplierId(),
                    null, "你发起的竞拍未通过审核。建议你修改内容后重新发起。", "用户Id：" + auction.getApplierId() + "申请竞拍:" + auction.getId() + "失败",
                    vo.getId(), MessageLogType.AUCTION, true);
            pmv.addExtras("skipType", "0");
        }
        //推送信息
        ac.publishEvent(new PushEvent(pmv));
    }

    @Override
    public Auction getAuction(Long id, boolean relation) {
        Auction auction = baseRepository.getObjById(Auction.class, id);
        if (relation) {
            assembleService.assembleObject(auction);
            this.addAuctionViewCount(id);
        }
        return auction;
    }

    @Override
    public Auction getAuction(Long id) {
        return this.getAuction(id, false);
    }

    @Override
    public CutPage<Auction> queryUserJoinAuction(String userId, Integer page, Integer limit) {
        String hql = "select distinct(auctionId) from AuctionItemOrder where status not in :status and userId = :userId order by payTime desc";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("userId", userId);
        paramMap.put("status", Arrays.asList(AuctionItemOrderStatus.CANCEL.getCode(), AuctionItemOrderStatus.CREATE.getCode()));
        List<Long> ids = baseRepository.queryHqlResult(hql, paramMap, 0, CutPage.MAX_COUNT);
        if (ids.size() <= 0) {
            return new CutPage<>(page, limit);
        }
        hql = "from Auction where id in :ids order by field(id,:ids)";
        paramMap.clear();
        paramMap.put("ids", ids);
        CutPage<Auction> cutPage = baseRepository.executeHql(hql, paramMap, page, limit);
        //设置用户
        this.setAuctionUser(cutPage.getiData());
        return cutPage;
    }

    @Override
    public CutPage<Auction> queryUserAuction(String userId, Integer page, Integer limit) {
        String hql = "from Auction where applierId = :userId and status in :status order by field(status,:status),finalTime desc";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("status", Arrays.asList(AuditStatus.PASS.getCode(), AuctionStatus.EVALUATE.getCode(), AuctionStatus.FINISH.getCode(), AuctionStatus.CANCEL.getCode()));
        CutPage<Auction> cutPage = baseRepository.executeHql(hql, map, page, limit);
        //设置用户
        this.setAuctionUser(cutPage.getiData());
        return cutPage;
    }

    @Override
    @Transactional
    public void setAuctionOrderEvaluate(String orderId) {
        String hql = "from AuctionOrder where orderId = :orderId";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("orderId", orderId);
        AuctionOrder auctionOrder = baseRepository.executeHql(hql, paramMap);
        if (auctionOrder == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "竞拍订单不存在");
        }
        if (!AuctionItemOrderStatus.PAY.getCode().equals(auctionOrder.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "竞拍订单状态异常");
        }
        //处理状态
        auctionOrder.setStatus(AuctionItemOrderStatus.EVALUATE.getCode());
        baseRepository.updateObj(auctionOrder);
        Auction auction = baseRepository.getObjById(Auction.class, auctionOrder.getAuctionId());
        auction.setStatus(AuctionStatus.EVALUATE.getCode());
        baseRepository.updateObj(auction);
        //将订单中的金额下发到
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, auction.getApplierId());
        UserRatio userRatio = userRatioService.getLatestByUser(auction.getApplierId());
        BigDecimal reward = auctionOrder.getPrice().multiply(userRatio.getAuctionRatio()).setScale(0, RoundingMode.HALF_UP);
        userAccountService.updateUserAccount(auction.getApplierId(), reward, AccountStatementType.AUCTION_REWARD, user.getNickName());
        UserCorrelative userCorrelative = correlativeService.queryUserCorrelativeByUserId(user.getId());
        userCorrelative.setAuction(userCorrelative.getAuction() + 1);
        baseRepository.updateObj(userCorrelative);
        if (userCorrelative.getAuction() > 10) {
            userRatio.setAuctionRatio(PropertiesConfig.getAuctionRewardProportion()[1]);
            userRatioService.save(userRatio);
        }
        this.pushOrderEvaluateMsg(auctionOrder);
    }


    @Override
    public void addAuctionViewCount(Long auctionId) {
        String hql = "update Auction set viewCount = viewCount + 1 where id = :id";
        Map<String, Object> map = new HashMap<>();
        map.put("id", auctionId);
        baseRepository.executeHql(hql, map);
    }

    @Override
    @Transactional
    public void disposeFinishAuction(Long auctionId) {
        Auction auction = baseRepository.getObjById(Auction.class, auctionId);
        //获取竞拍，判断竞拍是否为发布状态
        if (auction != null && AuditStatus.PASS.getCode().equals(auction.getStatus())) {
            //处理竞拍成功出价明细，生成订单，标记明细为待付款
            AuctionItem item = this.getAuctionItem(auction.getItemId());
            UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, auction.getApplierId());
            String auctionNickname = "";
            //将用户竞拍状态设置为无
            if (user != null) {
                auctionNickname = user.getNickName();
                if (item != null) {
                    //获取竞拍押金订单
                    AuctionDeposit deposit = this.getUserDeposit(auctionId, item.getUserId());
                    if (deposit != null && AuctionDepositStatus.PAY.getCode().equals(deposit.getStatus())) {
                        AuctionItemOrder order = new AuctionItemOrder();
                        order.setStatus(AuctionItemOrderStatus.CREATE.getCode());
                        order.setDepositId(deposit.getId());
                        order.setAuctionId(auctionId);
                        order.setAuctionItemId(auction.getItemId());
                        order.setPrice(item.getBidPrice());
                        BigDecimal price = order.getPrice().subtract(deposit.getDeposit());
                        order.setPayMoney(price.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : price);
                        order.setUserId(item.getUserId());
                        String orderId = OrderUtil.createOrderId(OrderUtil.AUCTION_ITEM);
                        order.setOrderId(orderId);
                        baseRepository.saveObj(order);
                        //创建通用订单
                        baseOrderService.addBaseOrder(orderId, OrderType.AUCTION_ORDER, order.getUserId(), auction.getApplierId());
                        item.setStatus(AuctionItemStatus.WAIT.getCode());
                        baseRepository.updateObj(item);
                        //将两个用户设为好友
                        messageCenterService.addBecomeFriends(auction.getApplierId(), deposit.getUserId());
                        //推送信息
                        PushMsgVO pmv = new PushMsgVO(auction.getApplierId(),
                                null, item.getNickname() + "已" + item.getBidPrice() + "元中拍，等待对方支付中", "用户Id：" + auction.getApplierId() + "竞拍:" + auction.getId() + "结束",
                                auction.getId(), MessageLogType.AUCTION, true);
                        pmv.addExtras("skipType", "1");
                        pmv.addExtras("auctionId", item.getAuctionId().toString());
                        PushMsgVO pmv2 = new PushMsgVO(item.getUserId(),
                                null, "你已成功竞拍" + user.getNickName() + "的约会，为保证约会及时进行，请在24小时内进行付款", "用户Id：" + item.getUserId() + "竞拍:" + auction.getId() + "成功",
                                auction.getId(), MessageLogType.AUCTION, true);
                        pmv2.addExtras("skipType", "1");
                        pmv2.addExtras("auctionId", item.getAuctionId().toString());
                        ac.publishEvent(new PushEvent(pmv, pmv2));
                    }
                } else {
                    //无人竞拍，推送信息
                    PushMsgVO pmv = new PushMsgVO(auction.getApplierId(),
                            null, "竞拍已结束，本次约会暂无人发起竞拍。建议选择时间重新发起。", "用户Id：" + auction.getApplierId() + "竞拍:" + auction.getId() + "结束",
                            auction.getId(), MessageLogType.AUCTION, true);
                    pmv.addExtras("skipType", "1");
                    pmv.addExtras("auctionId", auctionId.toString());
                    ac.publishEvent(new PushEvent(pmv));
                }
                user.removeState(BitStateUtil.OP_HAS_AUCTION_PROCESS);
                baseRepository.updateObj(user);
            }
            //处理竞拍未成功用户，退款
            this.disposeOutUser(auctionId, auction.getBidderId(), auctionNickname);
            auction.setStatus(AuctionStatus.FINISH.getCode());
            baseRepository.updateObj(auction);
        }
    }

    @Override
    @Transactional
    public void disposeAuctionFinish(Long auctionId) {
        //获取竞拍及竞拍的明细
        Auction auction = baseRepository.getObjById(Auction.class, auctionId);
        if (auction != null && AuditStatus.PASS.getCode().equals(auction.getStatus())) {
            UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, auction.getApplierId());
            user.removeState(BitStateUtil.OP_HAS_AUCTION_PROCESS);
            baseRepository.updateObj(user);
            auction.setFinalTime(new Date());
            auction.setStatus(AuctionStatus.FINISH.getCode());
            baseRepository.updateObj(auction);
            //处理竞拍成功
            this.disposeAuctionFinishSuccessUser(auction, user);
            //处理竞拍出局用户
            List<AuctionItemOrder> orders = this.queryUserOrder(auctionId, auction.getBidderId(),false);
            Set<String> userIds = orders.stream().map(o -> {
                o.setStatus(AuctionItemOrderStatus.WAIT_REFUND.getCode());
                baseRepository.updateObj(o);
                //创建退款任务
                taskListService.createTask(new TaskList(o.getUserId(), o.getOrderId(), TaskListType.AUCTION_ORDER_REFUND, null));
                return o.getUserId();
            }).collect(Collectors.toSet());
            this.pushAuctionFinishOutUserMsg(userIds, auctionId, user);
        }
    }

    //创建竞拍订单
    private AuctionOrder createAuctionOrder(Auction auction) {
        AuctionOrder order = new AuctionOrder();
        order.setStatus(AuctionItemOrderStatus.PAY.getCode());
        order.setOrderId(OrderUtil.createOrderId(OrderUtil.AUCTION));
        order.setBidUserId(auction.getBidderId());
        order.setAuctionId(auction.getId());
        order.setPrice(auction.getBidPrice());
        order.setUserId(auction.getApplierId());
        baseRepository.saveObj(order);
        BaseOrder baseOrder = new BaseOrder();
        baseOrder.setId(order.getOrderId());
        baseOrder.setType(OrderType.AUCTION_ORDER.getCode());
        baseOrder.setStatus(BaseOrderStatus.PAY.getCode());
        baseOrder.setReceiver(order.getUserId());
        baseOrder.setCreator(order.getBidUserId());
        baseRepository.saveObj(baseOrder);
        return order;
    }


    //推送竞拍成功用户消息
    private void disposeAuctionFinishSuccessUser(Auction auction, UserInfoBase auctionUser) {
        if (StringUtils.isNoneBlank(auction.getBidderId())) { //当有人竞拍时
            //创建竞拍订单
            this.createAuctionOrder(auction);
            //在订单明细设置订单id
            UserInfoBase bidder = baseRepository.getObjById(UserInfoBase.class, auction.getBidderId());
            //将两个用户设为好友
            messageCenterService.addBecomeFriends(auction.getApplierId(), auction.getBidderId());
            //推送信息
            PushMsgVO pmv = new PushMsgVO(auction.getApplierId(),
                    null, bidder.getNickName() + "以" + auction.getBidPrice() + "元中拍，你们可以开始聊天啦", "用户Id：" + auction.getApplierId() + "竞拍:" + auction.getId() + "结束",
                    auction.getId(), MessageLogType.AUCTION, true);
            pmv.addExtras("skipType", "1");
            pmv.addExtras("auctionId", auction.getId().toString());
            PushMsgVO pmv2 = new PushMsgVO(bidder.getId(),
                    null, "你已成功竞拍" + auctionUser.getNickName() + "的约会，你们可以开始聊天啦", "用户Id：" + bidder.getId() + "竞拍:" + auction.getId() + "成功",
                    auction.getId(), MessageLogType.AUCTION, true);
            pmv2.addExtras("skipType", "1");
            pmv2.addExtras("auctionId", auction.getId().toString());
            ac.publishEvent(new PushEvent(pmv, pmv2));
        } else {
            //无人竞拍，推送信息
            PushMsgVO pmv = new PushMsgVO(auction.getApplierId(),
                    null, "竞拍已结束，本次约会暂无人发起竞拍。建议选择时间重新发起。", "用户Id：" + auction.getApplierId() + "竞拍:" + auction.getId() + "结束",
                    auction.getId(), MessageLogType.AUCTION, true);
            pmv.addExtras("skipType", "1");
            pmv.addExtras("auctionId", auction.getId().toString());
            ac.publishEvent(new PushEvent(pmv));
        }
    }

    /**
     * 查询竞拍中的用户订单
     *
     * @param auctionId 订单id
     * @param firstUser 第一名用户
     * @param success   查询成功用户的订单
     * @return
     */
    private List<AuctionItemOrder> queryUserOrder(Long auctionId, String firstUser, boolean success) {
        StringBuilder hql = new StringBuilder(100);
        hql.append("from AuctionItemOrder where auctionId = :auctionId and userId ");
        hql.append(success ? " = " : " != ");
        hql.append(" :userId and status = :status");
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("auctionId", auctionId);
        paramMap.put("userId", firstUser);
        paramMap.put("status", AuctionItemOrderStatus.PAY.getCode());
        return baseRepository.queryHqlResult(hql, paramMap, 0, CutPage.MAX_COUNT);
    }

    @Transactional
    private void disposeOutUser(Long auctionId, String successUser, String auctionNickname) {
        CutPage<AuctionDeposit> cutPage = this.queryAuctionDeposit(auctionId, successUser, 0, CutPage.MAX_COUNT);
        for (AuctionDeposit deposit : cutPage.getiData()) {
            taskListService.createTask(new TaskList(deposit.getUserId(), deposit.getId().toString(), TaskListType.AUCTION_DEPOSIT_REFUND, null));
            PushMsgVO pmv = new PushMsgVO(deposit.getUserId(),
                    null, "本次未能成功竞拍" + auctionNickname + "的约会，保证金将在稍后退回钱包，期待你的下次参与。", "用户Id：" + deposit.getUserId() + "竞拍:" + auctionId + "失败",
                    deposit.getId(), MessageLogType.AUCTION, true);
            pmv.addExtras("skipType", "1");
            pmv.addExtras("auctionId", deposit.getAuctionId().toString());
            //推送信息
            ac.publishEvent(new PushEvent(pmv));
        }
    }

    @Override
    public boolean checkUserDeposit(Long auctionId, String userId) {
        AuctionDeposit userDeposit = this.getUserDeposit(auctionId, userId);
        return userDeposit != null && AuctionDepositStatus.PAY.getCode().equals(userDeposit.getStatus());
    }

    @Override
    public AuctionDeposit getUserDeposit(Long auctionId, String userId) {
        String hql = "from AuctionDeposit where auctionId = :auctionId and userId = :userId ";
        Map<String, Object> map = new HashMap<>();
        map.put("auctionId", auctionId);
        map.put("userId", userId);
        return baseRepository.executeHql(hql, map);
    }

    @Override
    public CutPage<AuctionDeposit> queryAuctionDeposit(Long auctionId, String excludeUser, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(100);
        hql.append("from AuctionDeposit where auctionId = :auctionId and status = 2 ");
        Map<String, Object> map = new HashMap<>();
        map.put("auctionId", auctionId);
        if (excludeUser != null) {
            hql.append(" and userId != :excludeUser");
            map.put("excludeUser", excludeUser);
        }
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    @Transactional
    public String createAuctionDeposit(Long auctionId, String userId) {
        Auction auction = baseRepository.getObjById(Auction.class, auctionId);
        if (auction == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "竞拍不存在");
        }
        if (!AuditStatus.PASS.getCode().equals(auction.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不在竞拍有效期间");
        }
        if (auction.getApplierId().equals(userId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您不能参加自己发起的竞拍！");
        }
        AuctionDeposit deposit = getUserDeposit(auctionId, userId);
        if (deposit != null) {
            if (AuctionDepositStatus.PAY.getCode().equals(deposit.getStatus())) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您已交过押金");
            }
            return deposit.getOrderId();
        }
        deposit = new AuctionDeposit();
        deposit.setStatus(AuctionDepositStatus.CREATE.getCode());
        deposit.setAuctionId(auctionId);
//        deposit.setDeposit(auction.getDeposit());
        String orderId = OrderUtil.createOrderId(OrderUtil.AUCTION_DEPOSIT);
        deposit.setOrderId(orderId);
        deposit.setPayMoney(deposit.getDeposit());
        deposit.setUserId(userId);
        baseRepository.saveObj(deposit);
        //创建通用订单
        baseOrderService.addBaseOrder(orderId, OrderType.AUCTION_DEPOSIT_ORDER, userId);
        return orderId;
    }

    @Override
    public AuctionDeposit getAuctionDepositByOrderId(String orderId) {
        String hql = "from AuctionDeposit where orderId = :orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        return baseRepository.executeHql(hql, map);
    }

    @Override
    public AuctionItemOrder getAuctionItemOrderByOrderId(String orderId) {
        String hql = "from AuctionItemOrder where orderId = :orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        return baseRepository.executeHql(hql, map);
    }

    @Override
    public AuctionOrder getAuctionOrderByUser(Long auctionId) {
        String hql = "from AuctionOrder where auctionId = :auctionId and status = :status";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("auctionId", auctionId);
        paramMap.put("status", AuctionItemOrderStatus.PAY.getCode());
        return baseRepository.executeHql(hql, paramMap);
    }

    @Override
    @Transactional
    public void disposeAuctionOrderCancel(AuctionItemOrder auctionItemOrder) {
        if (AuctionItemOrderStatus.CREATE.getCode().equals(auctionItemOrder.getStatus())) {
            AuctionDeposit deposit = this.getUserDeposit(auctionItemOrder.getAuctionId(), auctionItemOrder.getUserId());
            if (deposit != null && AuctionDepositStatus.PAY.getCode().equals(deposit.getStatus())) {
                Auction auction = baseRepository.getObjById(Auction.class, deposit.getAuctionId());
                if (auction != null) {
                    //获取押金，转到发起人账户
                    UserInfoBase user1 = baseRepository.getObjById(UserInfoBase.class, deposit.getUserId());
                    String nickName = user1 != null ? user1.getNickName() : "";
                    userAccountService.updateUserAccount(auction.getApplierId(), deposit.getDeposit(), AccountStatementType.AUCTION_CANCEL, nickName);
                    auction.setStatus(AuctionStatus.CANCEL.getCode());
                    baseRepository.updateObj(auction);
                    UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, auction.getApplierId());
                    //推送信息
                    String userNickName = user != null ? user.getNickName() : "";
                    PushMsgVO pmv = new PushMsgVO(auctionItemOrder.getUserId(), null,
                            "由于未在24小时内支付" + userNickName + "的约会订单，订单已自动取消。该约会所缴纳的保证金转入" + userNickName + "账户作为赔付。感谢你对约我的关注，期待你下一次参与。",
                            "用户Id：" + auctionItemOrder.getUserId() + "竞拍订单:" + auctionItemOrder.getOrderId() + "逾期",
                            auctionItemOrder.getId(), MessageLogType.AUCTION, true);
                    pmv.addExtras("skipType", "0");
                    PushMsgVO pmv2 = new PushMsgVO(auction.getApplierId(), null,
                            "由于" + nickName + "未及时支付订单，本次订单已自动取消。对方竞拍时缴纳的保证金已转入你的账户余额。感谢你对约我的关注，期待你下一次参与。",
                            "用户Id：" + auctionItemOrder.getUserId() + "竞拍订单:" + auctionItemOrder.getOrderId() + "逾期",
                            auctionItemOrder.getId(), MessageLogType.AUCTION, true);
                    pmv2.addExtras("skipType", "2");
                    ac.publishEvent(new PushEvent(pmv, pmv2));
                }
            }
            auctionItemOrder.setStatus(AuctionItemOrderStatus.CANCEL.getCode());
            baseRepository.updateObj(auctionItemOrder);
        }
    }

    @Override
    @Transactional
    public void createAuctionItem(AuctionItem auctionItem) {
        Auction auction = baseRepository.getObjById(Auction.class, auctionItem.getAuctionId());
        if (auction == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "竞拍不存在");
        }
        if (!AuditStatus.PASS.getCode().equals(auction.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不在竞拍有效期间");
        }
        if (!this.checkUserDeposit(auctionItem.getAuctionId(), auctionItem.getUserId())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "请支付押金再操作");
        }
        //获取出价金额，出价金额大于起拍金额且出价金额大于上次出价金额
        if (auction.getBidPrice().compareTo(auctionItem.getBidPrice()) >= 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "当前最新出价是" + auction.getBidPrice() + "元，建议你重新出价");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, auctionItem.getUserId());
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        //创建新出价明细
        AuctionItem item = new AuctionItem();
        item.setUserId(auctionItem.getUserId());
        item.setBidPrice(auctionItem.getBidPrice());
        item.setAuctionId(auctionItem.getAuctionId());
        item.setHeadImg(user.getUserPhotoHead());
        item.setNickname(user.getNickName());
        item.setStatus(AuctionItemStatus.FIRST.getCode());
        baseRepository.saveObj(item);
        //获取上次出价明细，处理状态，并发送出局通知
        AuctionItem old = this.getAuctionItem(auction.getItemId());
        if (old != null) {
            old.setStatus(AuctionItemStatus.OUT.getCode());
            baseRepository.updateObj(old);
            //如果不是同一个人时推送
            if (!item.getUserId().equals(old.getUserId())) {
                //推送信息
                PushMsgVO pmv = new PushMsgVO(old.getUserId(),
                        null, "你的出价已被超过，建议你重新出价。",
                        "用户Id：" + old.getUserId() + "参与竞拍:" + old.getAuctionId() + "被用户Id：" + item.getUserId() + "超越",
                        old.getAuctionId(), MessageLogType.AUCTION, true);
                pmv.addExtras("skipType", "1");
                pmv.addExtras("auctionId", old.getAuctionId().toString());
                ac.publishEvent(new PushEvent(pmv));
            }
        }
        //记录最新出价信息
        auction.setBidPrice(auctionItem.getBidPrice());
        auction.setBidderId(user.getId());
        auction.setItemId(item.getId());
        auction.setBidCount(auction.getBidCount() + 1);
        baseRepository.updateObj(auction);
    }

    @Override
    @Transactional
    public AuctionItemOrder createAuctionItemOrder(AuctionItem auctionItem) {
        Auction auction = baseRepository.getObjById(Auction.class, auctionItem.getAuctionId());
        if (auction == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "竞拍不存在");
        }
        if (!AuditStatus.PASS.getCode().equals(auction.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不在竞拍有效期间");
        }
        //获取出价金额，出价金额大于起拍金额且出价金额大于上次出价金额
        int compareTo = auction.getBidPrice().compareTo(auctionItem.getBidPrice());
        if (auction.getBidCount() > 0 && compareTo >= 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "当前最新出价是" + auction.getBidPrice() + "元，建议你重新出价");
        } else if (auction.getBidCount() == 0 && compareTo > 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "当前最新出价是" + auction.getBidPrice() + "元，建议你重新出价");
        }
        //创建竞拍明细订单
        AuctionItemOrder order = new AuctionItemOrder();
        order.setUserId(UserContext.getUserId());
        order.setStatus(AuctionItemOrderStatus.CREATE.getCode());
        order.setAuctionId(auction.getId());
        order.setOrderId(OrderUtil.createOrderId(OrderUtil.AUCTION_ITEM));
        //设置金额，如果没有竞拍过，则按当前的竞拍金额，竞拍过则只需补差价
        AuctionItem latestAuctionItemByUser = this.getLatestAuctionItemByUser(auction.getId(), UserContext.getUserId());
        BigDecimal price = auctionItem.getBidPrice();
        if (latestAuctionItemByUser != null) {
            price = price.subtract(latestAuctionItemByUser.getBidPrice());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "当前最新出价是" + auction.getBidPrice() + "元，建议你重新出价");
            }
        }
        order.setPrice(price);
        order.setPayMoney(order.getPrice());
        baseRepository.saveObj(order);
        baseOrderService.addBaseOrder(order.getOrderId(), OrderType.AUCTION_ITEM_ORDER, order.getUserId(), auction.getApplierId());
        return order;
    }

    @Override
    public AuctionItem getAuctionItem(Long itemId) {
        return baseRepository.getObjById(AuctionItem.class, itemId);
    }

    //获取用户最新的竞拍明细
    private AuctionItem getLatestAuctionItemByUser(Long auctionId, String userId) {
        String hql = "from AuctionItem where auctionId = :auctionId and userId = :userId order by bidPrice desc";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("auctionId", auctionId);
        paramMap.put("userId", userId);
        return baseRepository.executeHql(hql, paramMap);
    }

    @Override
    public CutPage<AuctionItem> queryActionItems(Long auctionId, Integer page, Integer limit) {
        String hql = "from AuctionItem where auctionId = :auctionId order by bidPrice desc,created";
        Map<String, Object> map = new HashMap<>();
        map.put("auctionId", auctionId);
        CutPage<AuctionItem> cutPage = baseRepository.executeHql(hql, map, page, limit);
        //获取昵称头像等信息
        List<AuctionItem> auctionItems = cutPage.getiData();
        List<String> userIds = auctionItems.stream().map(AuctionItem::getUserId).collect(Collectors.toList());
        Map<String, UserInfoBase> userMap = userInfoBaseService.queryUserMap(userIds);
        final boolean ifCoding = !UserContext.isSuperManager();
        auctionItems.forEach(i -> {
            UserInfoBase user = userMap.get(i.getUserId());
            if (user != null) {
                i.setNickname(ifCoding && !StringUtils.equals(UserContext.getUserId(), user.getId()) ? TextUtil.codingNickname(user.getNickName()) : user.getNickName());
                i.setHeadImg(user.getUserPhotoHead());
            } else {
                i.setNickname("***");
                i.setHeadImg("");
            }
        });
        return cutPage;
    }

    @Override
    public List<Auction> queryFinishAuction() {
        String hql = "from Auction where status = :status and finalTime <= NOW()";
        Map<String, Object> map = new HashMap<>();
        map.put("now", new Date());
        map.put("status", AuditStatus.PASS.getCode());
        return baseRepository.queryHqlResult(hql, map, 0, CutPage.MAX_COUNT);
    }

    @Override
    public List<AuctionItemOrder> queryOverdueAuctionOrder(Integer status, Integer hour) {
        String hql = "from AuctionItemOrder where status = :status and timesTampDiff(HOUR,payTime,NOW()) >= :hour";
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("hour", hour);
        return baseRepository.queryHqlResult(hql, map, 0, CutPage.MAX_COUNT);
    }

    @Override
    @Transactional
    public void setDepositOrderPaySuccess(String orderId) {
        AuctionDeposit deposit = this.getAuctionDepositByOrderId(orderId);
        if (deposit != null && AuctionDepositStatus.CREATE.getCode().equals(deposit.getStatus())) {
            deposit.setStatus(AuctionDepositStatus.PAY.getCode());
            deposit.setPayTime(new Date());
            baseRepository.updateObj(deposit);
        }
    }

    @Override
    @Transactional
    public void setAuctionItemOrderPaySuccess(String orderId) {
        AuctionItemOrder order = this.getAuctionItemOrderByOrderId(orderId);
        if (order != null && AuctionItemOrderStatus.CREATE.getCode().equals(order.getStatus())) {
            order.setStatus(AuctionItemOrderStatus.PAY.getCode());
            order.setPayTime(new Date());
            this.disposeAuctionItem(order);
            baseRepository.updateObj(order);
            /*AuctionItem item = this.getAuctionItem(order.getAuctionItemId());
            item.setStatus(AuctionItemStatus.PAY.getCode());
            baseRepository.updateObj(item);
            Auction auction = baseRepository.getObjById(Auction.class, order.getAuctionId());
            //推送信息
            PushMsgVO pmv = new PushMsgVO(auction.getApplierId(), null,
                    item.getNickname() + "已成功支付本次竞拍的剩余款项，建议你及时与对方联系约定约会时间。",
                    "用户Id：" + order.getUserId() + "支付竞拍订单:" + orderId,
                    order.getId(), MessageLogType.AUCTION, true);
            pmv.addExtras("skipType", "3");
            pmv.addExtras("userId", item.getUserId());
            pmv.addExtras("nickname", item.getNickname());
            ac.publishEvent(new PushEvent(pmv));*/
        }
    }

    @Override
    public Map<String, Auction> queryAuctionMapByUser(Collection userIds) {
        if (userIds.size() <= 0) {
            return new HashMap<>();
        }
        String hql = "from Auction where status = :status and applierId in :userIds ";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("status", AuditStatus.PASS.getCode());
        paramMap.put("userIds", userIds);
        List<Auction> result = baseRepository.queryHqlResult(hql, paramMap, 0, CutPage.MAX_COUNT);
        return result.stream().collect(Collectors.toMap(Auction::getApplierId, a -> a));
    }

    @Override
    public List<Auction> queryProceedAuction(String keyword) {
        StringBuilder sql = new StringBuilder(100);
        Map<String, Object> paramMap = baseRepository.paramMap();
        sql.append("SELECT a.* FROM auction a LEFT JOIN user_info_base u ON(a.applier_id = u.id) WHERE a.status = :status ");
        if (StringUtils.isNoneBlank(keyword)) {
            sql.append(" AND (u.nick_name like :keyword OR a.site like :keyword) ");
            paramMap.put("keyword", "%" + keyword + "%");
        }
        sql.append(" ORDER BY create_time DESC");
        paramMap.put("status", AuditStatus.PASS.getCode());
        List<Auction> auctions = baseRepository.querySqlResult(sql, paramMap, Auction.class, 0, CutPage.MAX_COUNT);
        this.setAuctionUser(auctions);
        return auctions;
    }

    @Override
    public CutPage<AuctionOrder> queryAllAuctionOrder(Long auctionId, Integer state, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(100);
        hql.append("from AuctionOrder where 1 = 1 ");
        Map<String, Object> map = baseRepository.paramMap();
        if (auctionId != null) {
            hql.append(" and auctionId = :auctionId ");
            map.put("auctionId", auctionId);
        }
        if (state != null) {
            hql.append(" and status = :state ");
            map.put("state", state);
        }
        hql.append(" order by created desc");
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public CutPage<AuctionItemOrder> queryAuctionItemOrder(Long auctionId, String userId, Integer state, Long auctionItemId, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(100);
        Map<String, Object> map = baseRepository.paramMap();
        hql.append("from AuctionItemOrder where ");
        if (auctionItemId != null){
            hql.append(" auctionItemId = :auctionItemId ");
            map.put("auctionItemId", auctionItemId);
        }else {
            hql.append(" auctionId = :auctionId and status in :state ");
            map.put("auctionId", auctionId);
            if (Objects.equals(state, 1)) {
                hql.append("  and userId = :userId ");
                map.put("userId", userId);
                map.put("state", Arrays.asList(AuctionItemOrderStatus.PAY.getCode(), AuctionItemOrderStatus.EVALUATE.getCode()));
            } else {
                map.put("state", Arrays.asList(AuctionItemOrderStatus.WAIT_REFUND.getCode(), AuctionItemOrderStatus.REFUND_SUCCESS.getCode()));
            }
        }
        hql.append(" order by created desc");
        return baseRepository.executeHql(hql, map, page, limit);
    }

    //处理竞拍明细
    private void disposeAuctionItem(AuctionItemOrder order) {
        //创建一个新的明细
        AuctionItem item = new AuctionItem();
        item.setAuctionId(order.getAuctionId());
        item.setUserId(order.getUserId());
        //获取最高出价明细
        AuctionItem latestAuctionItemByUser = this.getLatestAuctionItemByUser(order.getAuctionId(), order.getUserId());
        BigDecimal bidPrice = order.getPrice();
        if (latestAuctionItemByUser != null) {
            bidPrice = latestAuctionItemByUser.getBidPrice().add(bidPrice);
            item.setPrevId(latestAuctionItemByUser.getId());
            item.setPrevBidPrice(latestAuctionItemByUser.getBidPrice());
        }
        item.setBidPrice(bidPrice);
        //处理竞拍金额
        Auction auction = baseRepository.getObjById(Auction.class, order.getAuctionId());
        if (auction != null) {
            //比较当前竞拍中的金额
            if (auction.getBidPrice().compareTo(item.getBidPrice()) >= 0 && auction.getBidCount() > 0) {
                //竞拍明细中的金额少，推送被超消息
                item.setStatus(AuctionItemStatus.OUT.getCode());
                this.pushAuctionOutMsg(auction.getId(), auction.getBidderId(), order.getUserId());
            } else {
                item.setStatus(AuctionItemStatus.FIRST.getCode());
                auction.setBidderId(order.getUserId());
                auction.setBidPrice(item.getBidPrice());
                //设置原来的竞标未出局
                AuctionItem auctionItem = this.getAuctionItem(auction.getItemId());
                if (auctionItem != null) {
                    auctionItem.setStatus(AuctionItemStatus.OUT.getCode());
                    baseRepository.updateObj(auctionItem);
                }
                //推送领先信息
                this.pushAuctionOutMsg(auction.getId(), order.getUserId(), auction.getBidderId());
            }
            baseRepository.saveObj(item);
            if (AuctionItemStatus.FIRST.getCode().equals(item.getStatus())) {
                auction.setItemId(item.getId());
            }
            auction.setBidCount(auction.getBidCount() + 1);
            baseRepository.updateObj(auction);
            order.setAuctionItemId(item.getId());
            this.pushAuctionBidMsg(auction.getId(), auction.getApplierId(), order.getUserId());
        }
    }

    //推送竞拍出局消息
    private void pushAuctionOutMsg(Long auctionId, String firstUser, String outUser) {
        //不是自己的情况推送
        if (!StringUtils.equalsIgnoreCase(firstUser, outUser)) {
            PushMsgVO pmv = new PushMsgVO(outUser,
                    null, "你的出价已被超过，建议你重新出价。",
                    "用户Id：" + outUser + "参与竞拍:" + auctionId + "被用户Id：" + firstUser + "超越",
                    auctionId, MessageLogType.AUCTION, true);
            pmv.addExtras("skipType", "1");
            pmv.addExtras("auctionId", auctionId.toString());
            ac.publishEvent(new PushEvent(pmv));
        }
    }

    /**
     * 推送竞拍被竞拍消息
     *
     * @param auctionId 竞拍Id
     * @param userId    竞拍发起人
     * @param bidUserId 出价人
     */
    private void pushAuctionBidMsg(Long auctionId, String userId, String bidUserId) {
        //不是自己的情况推送
        if (!StringUtils.equalsIgnoreCase(userId, bidUserId)) {
            PushMsgVO pmv = new PushMsgVO(userId,
                    null, "你的竞拍有人出价了！快去看看",
                    "用户Id：" + bidUserId + " 参与竞拍:" + auctionId,
                    auctionId, MessageLogType.AUCTION, true);
            pmv.addExtras("skipType", "1");
            pmv.addExtras("auctionId", auctionId.toString());
            ac.publishEvent(new PushEvent(pmv));
        }
    }

    //推送出局用户消息
    private void pushAuctionFinishOutUserMsg(Set<String> userIds, Long auctionId, final UserInfoBase auctionUser) {
        userIds.forEach(id -> {
            PushMsgVO pmv = new PushMsgVO(id,
                    null, "本次未能成功竞拍" + auctionUser.getNickName() + "的约会，竞拍的金额原路退还到账户，期待你的下次参与。", "用户Id：" + id + "竞拍:" + auctionUser.getId() + "失败",
                    auctionId, MessageLogType.AUCTION, true);
            pmv.addExtras("skipType", "1");
            pmv.addExtras("auctionId", auctionId.toString());
            //推送信息
            ac.publishEvent(new PushEvent(pmv));
        });
    }

    //评价成功推送消息
    private void pushOrderEvaluateMsg(AuctionOrder auctionOrder) {
        //推送信息
        PushMsgVO pmv = new PushMsgVO(auctionOrder.getUserId(),
                null, "有人已对你的约会进行评价，本次接单收益已结算到钱包。", "用户Id：" + auctionOrder.getBidUserId() + "对竞拍订单:" + auctionOrder.getOrderId() + "进行评价",
                auctionOrder.getId(), MessageLogType.AUCTION, true);
        pmv.addExtras("skipType", "1");
        pmv.addExtras("auctionId", auctionOrder.getAuctionId().toString());
        ac.publishEvent(new PushEvent(pmv));
    }

}
