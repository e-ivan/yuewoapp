package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.SystemParameters;
import com.war4.enums.*;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.*;
import com.war4.service.*;
import com.war4.util.BitStateUtil;
import com.war4.util.DateUtil;
import com.war4.util.OrderUtil;
import com.war4.vo.PushMsgVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.war4.base.PropertiesConfig.*;

/**
 * Created by Administrator on 2016/12/7.
 */
@Service
public class VideoChatServiceImpl extends BaseService implements VideoChatService {

    @Autowired
    private UserInfoBaseServiceImpl userInfoBaseService;
    @Autowired
    private BaseOrderService baseOrderService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private PayUtilService payUtilService;
    @Autowired
    private UserRatioService userRatioService;
    @Autowired
    private UserCorrelativeService correlativeService;
//    @Autowired
//    private  auctionService;

//    public static final String password = "@!yuewo@77778888";


    @Override
    @Transactional
    public void createVideoChat(String userId) {

        //查询个人视频
        StringBuilder hql_video = new StringBuilder(200);
        Map<String, Object> map_video = new HashMap<>();
        hql_video.append("from UserVideo where applierId = :applierId");
        map_video.put("applierId", userId);
        UserVideo userVideo = baseRepository.executeHql(hql_video, map_video);
        if (userVideo == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "个人视频还没提交！");
        }

        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();
        hql.append("from VideoChat where userId = :userId ");
        map.put("userId", userId);
        VideoChat videoChat = baseRepository.executeHql(hql, map);

        if (videoChat == null) {
            VideoChat vc = new VideoChat();
            vc.setUserId(userId);
            vc.setVideoId(userVideo.getId());
            vc.setOnOff(true);//默认打开
            vc.setStatus(userVideo.getStatus());
            baseRepository.saveObj(vc);
        } else {
            videoChat.setVideoId(userVideo.getId());
            videoChat.setStatus(userVideo.getStatus());
            baseRepository.updateObj(videoChat);
        }

        UserInfoBase userInfoBase = baseRepository.getObjById(UserInfoBase.class, userId);
        //如果个人视频已审核通过，则推送消息给用户
        if (AuditStatus.PASS.getCode().equals(userVideo.getStatus())) {
            userInfoBase.addState(BitStateUtil.OP_HAS_VIDEO_CHAT_PASS);
            PushMsgVO pmv = new PushMsgVO(userVideo.getApplierId(), userVideo.getAuditorId(), SystemParameters.MESSAGE_VIDEO_CHAT_AUDIT, "恭喜您的个人视频已通过审核！", MessageLogType.VIDEO_CHAT, true);
            ac.publishEvent(new PushEvent(pmv));
        } else {
            userInfoBase.addState(BitStateUtil.OP_HAS_VIDEO_CHAT_REVIEW);
        }
        baseRepository.updateObj(userInfoBase);
    }

    @Override
    @Transactional
    public void updateVideoChat(VideoChat videoChat) {
        Map<String, Object> map = new HashMap<>();
        String hql = "from VideoChat where userId = :userId ";
        map.put("userId", videoChat.getUserId());
        VideoChat v = baseRepository.executeHql(hql, map);
        if (v == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "聊天室不存在!");
        }
//        v.setChatNum(videoChat.getChatNum());
        v.setOnOff(videoChat.getOnOff());
        baseRepository.updateObj(v);
    }

    @Override
    public CutPage<VideoChat> queryVideoChatList(Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(150);
        Map<String, Object> map = new HashMap<>();
        hql.append("from VideoChat where status =:status and onOff =:onOff and chatState =:chatState order by createTime desc ");
        map.put("chatState", VideoChatStatus.OFFLINE.getCode());
        map.put("onOff", true);
        map.put("status", AuditStatus.PASS.getCode());
        CutPage<VideoChat> cutPage = baseRepository.executeHql(hql, map, page, limit);
        List<VideoChat> videoChats = cutPage.getiData();
        //获取用户信息
        List<String> ids = videoChats.stream().map(VideoChat::getUserId).collect(Collectors.toList());
        Map<String, UserInfoBase> userMap = userInfoBaseService.queryUserMap(ids);
        videoChats.forEach(v -> {
            UserInfoBase user = userMap.get(v.getUserId());
            if (user != null) {
                v.setUserPhotoHead(user.getUserPhotoHead());
                v.setName(user.getNickName());
            }
        });
        return cutPage;
    }

    @Override
    public CutPage<VideoChat> queryVideoChatListForServer(String keyword, Integer page, Integer limit) {
        StringBuilder sql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();
        sql.append("SELECT v.* FROM video_chat v LEFT JOIN user_info_base u ON v.user_id = u.id WHERE 1=1 ");

        sql.append("AND v.status = :status ");
        map.put("status", AuditStatus.PASS.getCode());

        if (StringUtils.isNotBlank(keyword)) {
            sql.append(" AND (v.user_id like :keyword or u.nick_name like :keyword ) ");
            map.put("keyword", "%" + keyword + "%");
        }

        sql.append(" ORDER BY v.create_time desc ");
        CutPage<VideoChat> cutPage = baseRepository.executeSql(sql.toString(), map, VideoChat.class, page, limit);
        List<VideoChat> videoChats = cutPage.getiData();
        List<String> ids = videoChats.stream().map(VideoChat::getUserId).collect(Collectors.toList());
        Map<String, UserInfoBase> userMap = userInfoBaseService.queryUserMap(ids);
        videoChats.forEach(v -> {
            UserInfoBase user = userMap.get(v.getUserId());
            if (user != null) {
                v.setName(user.getNickName());
            }
        });
        return cutPage;
    }

    @Override
    public CutPage<VideoChatItem> queryMyCreatedVideoChat(String userId, Integer page, Integer limit) {

        String hql = "from VideoChatItem where createUserId =:createUserId order by createTime desc ";
        Map<String, Object> map = new HashMap<>();
        map.put("createUserId", userId);
        CutPage<VideoChatItem> videoChatItemCutPage = baseRepository.executeHql(hql, map, page, limit);
        for (VideoChatItem videoChatItem : videoChatItemCutPage.getiData()) {
            UserInfoBase userInfoBase = baseRepository.getObjById(UserInfoBase.class, videoChatItem.getAcceptUserId());
            if (userInfoBase != null) {
                videoChatItem.setSex(userInfoBase.getSex());
                videoChatItem.setName(userInfoBase.getNickName());
                videoChatItem.setUserPhotoHead(userInfoBase.getUserPhotoHead());
                videoChatItem.setAge(userInfoBase.getAge().intValue());
            }
        }

        return videoChatItemCutPage;

    }

    @Override
    public CutPage<VideoChatItem> queryMyAcceptVideoChat(String userId, Integer page, Integer limit) {

        String hql = "from VideoChatItem where acceptUserId =:acceptUserId order by createTime desc ";
        Map<String, Object> map = new HashMap<>();
        map.put("acceptUserId", userId);
        CutPage<VideoChatItem> videoChatItemCutPage = baseRepository.executeHql(hql, map, page, limit);
        for (VideoChatItem videoChatItem : videoChatItemCutPage.getiData()) {
            UserInfoBase userInfoBase = baseRepository.getObjById(UserInfoBase.class, videoChatItem.getCreateUserId());
            if (userInfoBase != null) {
                videoChatItem.setSex(userInfoBase.getSex());
                videoChatItem.setAge(userInfoBase.getAge().intValue());
                videoChatItem.setName(userInfoBase.getNickName());
                videoChatItem.setUserPhotoHead(userInfoBase.getUserPhotoHead());
            }
        }
        return videoChatItemCutPage;

    }

    @Override
    public CutPage<VideoChatItem> queryVideoChatItemList(Long chatId, Integer payType, Integer state, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(50);
        hql.append(" from VideoChatItem where 1 = 1");
        Map<String, Object> map = baseRepository.paramMap();
        if (chatId != null) {
            hql.append(" and cid = :chatId");
            map.put("chatId",chatId);
        }
        if (state != null) {
            hql.append(" and status = :state");
            map.put("state", state);
        }
        OrderPayType type = OrderPayType.getByCode(payType);
        if (type != null) {
            hql.append(" and payType = :payType");
            map.put("payType", type.getValue());
        }
        hql.append(" order by createTime desc");
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public VideoChat getUserVideoChat(String userId) {
        String hql = "from VideoChat where userId =:userId ";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        return baseRepository.executeHql(hql, map);
    }

    @Override
    public void updateVideoChatSetting(String userId, Boolean onOff) {

        String hql = "from VideoChat where userId =:userId ";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        VideoChat videoChat = baseRepository.executeHql(hql, map);
        if (videoChat == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "视频聊天没有开通！");
        }
        if (!AuditStatus.PASS.getCode().equals(videoChat.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "视频聊天还没审核通过！");
        }

        UserInfoBase userInfoBase = baseRepository.getObjById(UserInfoBase.class, userId);
        if (onOff) {
            userInfoBase.addState(BitStateUtil.OP_HAS_OPEN_VIDEO_CHAT);
        } else {
            userInfoBase.removeState(BitStateUtil.OP_HAS_OPEN_VIDEO_CHAT);
        }
        baseRepository.updateObj(userInfoBase);

        videoChat.setOnOff(onOff);
        baseRepository.updateObj(videoChat);
    }

    @Override
    @Transactional
    public void addVideoChatReport(Long cid, String createUserId, String reportUserId, Integer type) {

        String hql = "from VideoChatReport where cid =:cid ";
        Map<String, Object> map = new HashMap<>();
        map.put("cid", cid);
        VideoChatReport videoChatReport = baseRepository.executeHql(hql, map);
        if (videoChatReport != null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能重复举报！");
        }

        VideoChatReport vr = new VideoChatReport();
        vr.setCid(cid);
        vr.setCreateUserId(createUserId);
        vr.setReportUserId(reportUserId);
        vr.setType(type);
        baseRepository.saveObj(vr);

    }

    @Override
    @Transactional
    public Map<String, Object> createMyVideoChatOrder(Long cid, String createUserId, String acceptUserId, String vid, Date serverTime) {
        VideoChat videoChat = baseRepository.getObjById(VideoChat.class, cid);
        if (videoChat == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "对方还没视频认证！");
        }
        if (StringUtils.equals(createUserId, videoChat.getUserId())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能与自己视频聊天");
        }
        if (!AuditStatus.PASS.getCode().equals(videoChat.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "对方还没通过视频审核！");
        }
        if (VideoChatStatus.ONLINE.getCode().equals(videoChat.getChatState())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "对方正在视频中，请稍候再试");
        }
        UserInfoBase createUser = baseRepository.getObjById(UserInfoBase.class, createUserId);
        boolean first = !createUser.getHasVideoChatOnce();


        Date now = new Date();
        if (serverTime != null && DateUtil.intervalTime(now, serverTime) > 5) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "服务器繁忙！");
        }

//        Long payAndStartDuration = (now.getTime() - startTime.getTime()) / 1000;//得出相差多少秒，取整
        /*if(payAndStartDuration > 10 || payAndStartDuration < -10){
            throw  new BusinessException(CommonErrorResult.BUSINESS_ERROR,"网络延时严重！");
        }*/

        if (videoChat.getLastPayTime() != null) {
            Long time = DateUtil.intervalTime(now, videoChat.getLastPayTime());
            //如果支付时间少于5秒，则报错
            if (time < 5) {
                throw new BusinessException(CommonErrorResult.SECRET_FAIL, "操作时间太频繁！");
            }
        }

        //视频一次，加一次聊天记录,而且变成视频中
        videoChat.setLastPayTime(now);
        videoChat.setChatState(VideoChatStatus.ONLINE.getCode());
        videoChat.setChatNum(videoChat.getChatNum() + 1);
        videoChat.setOrderCount(videoChat.getOrderCount() + 1);
        videoChat.setReloadTime(now);
        baseRepository.updateObj(videoChat);

        VideoChatItem videoChatItem = new VideoChatItem();
        videoChatItem.setCid(cid);
        videoChatItem.setCreateUserId(createUserId);
        videoChatItem.setAcceptUserId(videoChat.getUserId());
        videoChatItem.setVid(vid);
        videoChatItem.setStartTime(now);
        videoChatItem.setStatus(VideoChatOrderStatus.PAY.getCode());
        videoChatItem.setPayTime(now);
        videoChatItem.setDuration(1L);
        /*BigDecimal dur = new BigDecimal(Math.abs(payAndStartDuration));
        BigDecimal sixtySecond = new BigDecimal("60");
        BigDecimal dur_60 = dur.divide(sixtySecond, BigDecimal.ROUND_UP);
        if (dur_60.longValue() == 0) {
            BigDecimal one = new BigDecimal(1);
        } else {
            videoChatItem.setDuration(dur_60.longValue());
        }*/

        String orderId = OrderUtil.VIDEO_CHAT + OrderUtil.getUUID();
        videoChatItem.setOrderId(orderId);
        //判断是否首次 首次3分钟免费
        //扣除起步价
        videoChatItem.setPayType(first ? OrderPayType.SALE.getValue() : OrderPayType.BALANCE.getValue());
        videoChatItem.setPayMoney(first ? BigDecimal.ZERO : getVideoChatCostStartStep());
        baseRepository.saveObj(videoChatItem);
        baseOrderService.addBaseOrder(orderId, OrderType.VIDEO_CHAT_ORDER,createUserId,acceptUserId);

        //用户扣费
        userAccountService.updateUserAccount(createUserId, videoChatItem.getPayMoney(), AccountStatementType.VIDEO_CHAT_COST);
        //给对方分成利润
        UserRatio userRatio = userRatioService.getLatestByUser(videoChat.getUserId());
        BigDecimal profit = userRatio.getVideoChatRatio().multiply(getVideoChatCostStartStep());
        userAccountService.updateUserAccount(videoChat.getUserId(), profit, AccountStatementType.VIDEO_CHAT_PROFIT);

        //设置成功支付
        try {
            payUtilService.payService(videoChatItem.getOrderId());
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "支付有问题！");
        }
        //首次聊天记录状态
        if (first) {
            createUser.addState(BitStateUtil.OP_HAS_VIDEO_CHAT_ONCE);
            baseRepository.updateObj(createUser);
        }
        UserAccount userAccount = userAccountService.getUserAccountByUserId(createUserId);
        //如果余额不多，则返回参数提醒
        return getMap(videoChatItem, userAccount, getVideoChatCostStartDuration());
    }

    @Override
    @Transactional
    public Map<String, Object> updateMyVideoChatOrder(Long chatId, Date now, String userId) {
        VideoChatItem videoChatItem = baseRepository.getObjById(VideoChatItem.class, chatId);
        if (videoChatItem == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "此聊天记录不存在！");
        }
        VideoChat videoChat = baseRepository.getObjById(VideoChat.class, videoChatItem.getCid());

        UserAccount userAccount = userAccountService.getUserAccountByUserId(videoChatItem.getCreateUserId());
        Date serverTimeNow = new Date();

        Long dura = DateUtil.getMinuteUp(videoChatItem.getStartTime(), serverTimeNow);

        //起步时间
        long surplus = getVideoChatCostStartDuration() - dura;
        if (surplus >= 0) {
            ++surplus;
            if (!videoChatItem.getDuration().equals(dura)) {
                videoChatItem.setPayTime(serverTimeNow);
            }
            //更新时长
            videoChatItem.setDuration(dura);
        } else {
            //起步时间用完
            Long minuteDown = DateUtil.getMinuteDown(videoChatItem.getPayTime(), serverTimeNow);
            if (minuteDown > 0) {
                videoChatItem.setPayTime(serverTimeNow);
            }
            surplus = minuteDown;
            BigDecimal currentPay = new BigDecimal(getVideoChatCostOneStep().longValue())
                    .multiply(new BigDecimal(minuteDown));
            videoChatItem.setDuration(dura);
            videoChatItem.setPayMoney(videoChatItem.getPayMoney().add(currentPay));
            //用户扣费
            userAccountService.updateUserAccount(videoChatItem.getCreateUserId(), currentPay, AccountStatementType.VIDEO_CHAT_COST);
            //给对方分成利润
            UserRatio userRatio = userRatioService.getLatestByUser(videoChatItem.getAcceptUserId());
            BigDecimal profit = userRatio.getVideoChatRatio().multiply(currentPay);
            userAccountService.updateUserAccount(videoChatItem.getAcceptUserId(), profit, AccountStatementType.VIDEO_CHAT_PROFIT);
        }

        baseRepository.updateObj(videoChatItem);
        videoChat.setReloadTime(serverTimeNow);
        videoChat.setLastPayTime(serverTimeNow);
        baseRepository.updateObj(videoChat);
        return getMap(videoChatItem, userAccount, (int) surplus);
    }

    private static Map<String, Object> getMap(VideoChatItem videoChatItem, UserAccount userAccount, Integer addTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("cid", videoChatItem.getId());
        map.put("orderId", videoChatItem.getOrderId());
        map.put("balance", userAccount.getBalance());
        map.put("timeLeft", addTime + userAccount.getBalance().divide(getVideoChatCostOneStep(), BigDecimal.ROUND_DOWN).intValue());
        System.out.println("map = " + map);
        return map;
    }

    @Override
    @Transactional
    public void updateVideoChatOver(String vid, String hangUpUserId) {
        String hql = "from VideoChatItem where vid = :vid order by createTime desc";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("vid", vid);
        VideoChatItem item = baseRepository.executeHql(hql, paramMap);
        if (item != null && item.getHangUpUserId() == null) {
            item.setHangUpUserId(hangUpUserId);
            baseRepository.updateObj(item);
            VideoChat videoChat = baseRepository.getObjById(VideoChat.class, item.getCid());
            if (videoChat != null) {
                videoChat.setChatState(VideoChatStatus.OFFLINE.getCode());
                baseRepository.updateObj(videoChat);
            }
        }
    }

    @Override
    public void setOrderPaySuccess(String orderId) {
        String hql = "from VideoChatItem where orderId = :orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        VideoChatItem videoChatItem = baseRepository.executeHql(hql, map);

        if (videoChatItem != null && VideoChatOrderStatus.CREATE.getCode().equals(videoChatItem.getStatus())) {
            videoChatItem.setStatus(AuctionItemOrderStatus.PAY.getCode());
            videoChatItem.setPayTime(new Date());
            baseRepository.updateObj(videoChatItem);
        }

    }


    @Override
    @Transactional
    public void setVideoChatOrderEvaluate(String orderId) {
        String hql = "from VideoChatItem where orderId = :orderId";
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        VideoChatItem videoChatItem = baseRepository.executeHql(hql, map);
        if (videoChatItem == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "视频聊天订单不存在");
        }
        if (!VideoChatOrderStatus.PAY.getCode().equals(videoChatItem.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "视频聊天订单状态异常");
        }
        videoChatItem.setStatus(VideoChatOrderStatus.EVALUATE.getCode());
        baseRepository.updateObj(videoChatItem);
        UserCorrelative userCorrelative = correlativeService.queryUserCorrelativeByUserId(videoChatItem.getAcceptUserId());
        userCorrelative.setVideoChat(userCorrelative.getVideoChat() + 1);
        baseRepository.updateObj(userCorrelative);
        if (userCorrelative.getVideoChat() > 10) {
            UserRatio userRatio = userRatioService.getLatestByUser(videoChatItem.getAcceptUserId());
            userRatio.setVideoChatRatio(getVideoChatCostProportion()[1]);
            userRatioService.save(userRatio);
        }
    }

    @Override
    public CutPage<VideoChat> queryVideoChatForChatState() {
        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();
        hql.append("from VideoChat where chatState = :chatState and timestampdiff(SECOND,reloadTime,NOW()) >= 120 ");
        map.put("chatState", VideoChatStatus.ONLINE.getCode());
        return baseRepository.executeHql(hql, map, 0, Integer.MAX_VALUE);
    }

    @Override
    public List<VideoChatItem> queryOverdueVideoOrder(Integer status, Integer hour) {
        String hql = "from VideoChatItem where status = :status and timesTampDiff(HOUR,startTime,NOW()) >= :hour";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("status", status);
        paramMap.put("hour", hour);
        return baseRepository.queryHqlResult(hql, paramMap, 0, CutPage.MAX_COUNT);
    }

    public static void main(String[] args) {

        try {
              /* 随便选两个时间 */
            String d1 = "2015-03-17 10:10:00";
            String d2 = "2015-03-17 10:10:59";

        /* 先转成毫秒并求差 */
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long m = sdf.parse(d2).getTime() - sdf.parse(d1).getTime();

        /* 根据你的需求进行单位转换 */
            System.out.println("相差毫秒数:" + m / 1000);
            System.out.println("相差天数:" + (m / (1000 * 60 * 60 * 24)));
//
//            BigDecimal profit = PropertiesConifig.getVideoChatCostProportion().multiply(PropertiesConifig.getVideoChatCostStartStep());
//
//            System.out.println(profit);

//            String sss = "azI6efapQK74Fpm486FWiw==";
//            String aaa = "2017-11-12";
//            String result = new String(DesUtil.encrypt(sss.getBytes(), password));
//
//            System.out.println(result);


            BigDecimal a = new BigDecimal(4);
            BigDecimal b = new BigDecimal(10);

            BigDecimal c = a.divide(b, BigDecimal.ROUND_UP);


            System.out.println(a);
            System.out.println(c);
//            System.out.println(a.intValue());

        } catch (Exception e) {

        }

    }

}
