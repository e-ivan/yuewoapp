package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.enums.*;
import com.war4.listener.event.ActivityBidBeginEvent;
import com.war4.listener.event.ActivityBidLotteryEvent;
import com.war4.pojo.*;
import com.war4.service.*;
import com.war4.util.DateUtil;
import com.war4.util.MapDistance;
import com.war4.util.NumberUtil;
import com.war4.vo.film.YingMovieInfoVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by yhy on 2016/10/28.
 */
@Component
public class TimeTaskService extends BaseService {

    @Autowired
    private UserVipSettingService userVipSettingService;

    @Autowired
    private UserVIPService userVIPService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private YinghezhongService yinghezhongService;

    @Autowired
    private KouDianYingService kouDianYingService;

    @Autowired
    private UserInfoBaseService userService;

    @Autowired
    private ActivityBidService activityBidService;

    @Autowired
    private TaskListService taskListService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private EvaluateSystemService evaluateSystemService;

    @Autowired
    private SystemDictionaryService systemDictionaryService;

    @Autowired
    private VideoChatService videoChatService;


    private static final int MAX_INTERNAL_USER_NUM = PropertiesConfig.getMinInternalUserNum();//最多内部用户
    private static final int MIN_INTERNAL_USER_NUM = PropertiesConfig.getMinInternalUserNum();//最少内部用户
    private static final long MIN_INTERNAL_USER_DISTANCE = PropertiesConfig.getMinInternalUserDistance();//最小距离（米）
    private static final long MAX_INTERNAL_USER_DISTANCE = PropertiesConfig.getMaxInternalUserDistance();//最大距离（米）
    private static final boolean ONLY_OPPOSITE = PropertiesConfig.isOnlyOpposite();//只设置异性
    private static final int EVALUATE_DAY = PropertiesConfig.getAuctionOrderDefaultEvaluateDay();//默认评价天数



    /*
        一个cron表达式有至少6个（也可能7个）有空格分隔的时间元素。
        按顺序依次为
        秒（0~59）

        分钟（0~59）

        小时（0~23）

        天（月）（0~31，但是你需要考虑你月的天数）

        月（0~11）

        天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）

        7.年份（1970－2099）


        字段   允许值   允许的特殊字符
        秒 	  	0-59 	  	, - * /
        分 	  	0-59 	  	, - * /
        小时 	0-23 	  	, - * /
        日期 	1-31 	  	, - * ? / L W C
        月份 	1-12 或者 JAN-DEC  , - * /
        星期 	1-7 或者 SUN-SAT   , - * ? / L C #
        年（可选）留空, 1970-2099 	  , - * /

        http://rainbowdesert.iteye.com/blog/2107220
     */

    //每月一点
    //正式环境每月送一次 0 0 0 0 * ?
    //测试环境每天送一次 0 0 0 * * ?
    /*@Scheduled(cron = "0 0 0 1 * ?")
    public void Task() {
        sendUserCoupon();
    }*/

    //更新天智创客影院排期
    //每天凌晨1点更新
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateCinemaPlan() {
        if (PropertiesConfig.isProduction()) {//非debug模式
            List<SystemDictionaryItem> cinemaMenu = systemDictionaryService.getSystemDictionaryBySN("cinemaMenu", true).getItems();
            boolean flag = false;
            for (SystemDictionaryItem item : cinemaMenu) {
                if (StringUtils.equals(item.getTitle(), "天智创客")) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                //保存用户短信信息
                try {
                    yinghezhongService.updateCinemaPlays();
                    List<YingMovieInfoVO> list = yinghezhongService.updateMovieInfo();
                    String str = "系统于 " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
                    if (list.size() > 0) {
                        int[] count = {1};
                        //自动更新电影信息
                        List<YingMovieInfoVO> success = list.stream().filter(m -> yinghezhongService.autoRelationKouMovie(m,count)).collect(Collectors.toList());
                        int i = list.size() - success.size();
                        if (i > 0) {//有未成功管理电影
                            smsService.sendAndSaveSMS(str + "新增[" + list.size() + "]条电影数据，其中有[" + i + "]条数据未自动关联成功，请尽快处理！", PropertiesConfig.getMovieAdminPhone(), MessageLogType.SYSTEM_UPDATE, null, null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String msg = "系统于 " + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "更新影院排期和电影信息失败！请进行手动操作";
                    smsService.sendAndSaveSMS(msg, PropertiesConfig.getSystemSendSmsPhone(), MessageLogType.SYSTEM_UPDATE, null, null);
                }
            }
        }
    }

    //更新抠电影的城市
    //每月凌晨两点更新
    @Scheduled(cron = "0 0 2 1 * ?")
    public void updateKouCity() {
        kouDianYingService.updateCity();
    }

    //更新抠电影影院数据
    //每月凌晨三点更新
    @Scheduled(cron = "0 0 3 1 * ?")
    public void updateKouCinema() {
        kouDianYingService.updateCinemaChannel();
    }

    //更新抠电影电影数据
    //每天凌晨1点
    @Scheduled(cron = "20 0 1 * * ?")
    public void updateKouMovie() {
        kouDianYingService.updateMovie();
    }

    //监听新用户注册表
    //每分钟处理一次
    @Scheduled(cron = "0 * * * * ?")
    public void listenNewUser() {
        //获取所有一分钟前的注册用户
        CutPage<NewUserRegister> cutPage = userService.queryUserRegister(NewUserRegister.CREATE, DateUtils.addMinutes(new Date(), -1), 0, CutPage.MAX_COUNT);
        for (NewUserRegister newUser : cutPage.getiData()) {
            UserInfoBase user = userService.getUserSimpleById(newUser.getUserId());
            if (user != null) {
                String userY = user.getY();//纬度
                String userX = user.getX();//经度
                if (userX != null && userY != null) {//经纬度不为空时执行
                    String distance = NumberUtil.getAroundVal(String.valueOf(MAX_INTERNAL_USER_DISTANCE), String.valueOf(MIN_INTERNAL_USER_DISTANCE), 0);
                    Map around = MapDistance.getAround(userY, userX, distance);
                    String num = NumberUtil.getAroundVal(String.valueOf(MAX_INTERNAL_USER_NUM), String.valueOf(MIN_INTERNAL_USER_NUM), 0);
                    //获取到注册用户的坐标
                    CutPage<UserInfoBase> internalUsers = userService.queryAllUserList(UserRoleType.COMMON_USER.getCode(), null,
                            null, null, 1, null, null,
                            ONLY_OPPOSITE ? getOpposite(user.getSex()) : null, "update", 0, Integer.valueOf(num));
                    String maxLng = (String) around.get("maxLng");//最大经度
                    String minLng = (String) around.get("minLng");//最小经度
                    String maxLat = (String) around.get("maxLat");//最大纬度
                    String minLat = (String) around.get("minLat");//最小纬度
                    //更新内部会员的坐标
                    for (UserInfoBase internalUser : internalUsers.getiData()) {
                        //计算指定距离区间内（随机）的经纬度范围，随机生成一个坐标
                        String lng = NumberUtil.getAroundVal(maxLng, minLng, 6);
                        String lat = NumberUtil.getAroundVal(maxLat, minLat, 6);
                        userService.updateUserLngAndLat(internalUser.getId(), lng, lat);
                    }
                    //将新用户标记为完成
                    userService.updateUserRegisterDone(newUser.getId(), "正常完成");
                } else if (DateUtil.intervalTime(newUser.getCreated(), new Date(), DateUtil.DAY) >= 5) {
                    //将新用户标记为完成
                    userService.updateUserRegisterDone(newUser.getId(), "超时任务");
                }
            } else {
                userService.updateUserRegisterDone(newUser.getId(), "用户删除");
            }
        }

    }


    //设置活动开始
    @Scheduled(cron = "5 * * * * ?")
    public void listenActivityBidBegin() {
        CutPage<ActivityBid> activityBidCutPage = activityBidService.queryActivityBidListByDate(ActivityBid.TYPE_ONEYUAN, ActivityBid.STATE_CREATE, new Date(), 0, Integer.MAX_VALUE);
        for (ActivityBid activityBid : activityBidCutPage.getiData()) {
            activityBidService.updateActivityBidState(activityBid.getId(), ActivityBid.STATE_GOING);
            //自动设置下一次活动
            ac.publishEvent(new ActivityBidBeginEvent(activityBid));
        }
    }

    //开始抽奖
    @Scheduled(cron = "10 * * * * ?")
    public void listenLotteryDraw() {
        CutPage<ActivityBid> activityBidCutPage = activityBidService.queryActivityBidListByBeginToLottery(ActivityBid.TYPE_ONEYUAN, ActivityBidStatus.BEGIN.getCode(), new Date(), 0, Integer.MAX_VALUE);
        for (ActivityBid activityBid : activityBidCutPage.getiData()) {
            activityBid.setState(ActivityBid.STATE_OVER);
            baseRepository.updateObj(activityBid);
            //事件推送 抽奖
            ac.publishEvent(new ActivityBidLotteryEvent(activityBid));
        }
    }


    //监听投标活动明细，处理过期订单
    //每分钟处理一次
    @Scheduled(cron = "2 * * * * ?")
    public void activityBidItemTimeout() {
        activityBidService.handleItemTimeout(15);
    }

    //处理一元夺票退款的任务列表
    //每15分钟扫描一次
    @Scheduled(cron = "10 0/15 * * * ?")
    public void disposeBidRefundTaskList() {
        CutPage<TaskList> cutPage = taskListService.queryTaskList(TaskList.CREATE, TaskListType.BID_REFUND, 0, 20);
        for (TaskList taskList : cutPage.getiData()) {
            ActivityBidItem item = baseRepository.getObjById(ActivityBidItem.class, taskList.getObjectId());
            if (item != null) {
                //退款操作
                userAccountService.updateUserAccount(item.getUserId(), item.getPayMoney(), AccountStatementType.BID_REFUND);
                taskListService.updateTaskFinish(taskList);
            }
        }
    }

    //处理竞拍退押金的任务
    //半个钟处理一次
//    @Scheduled(cron = "11 0/30 * * * ?")
    public void disposeAuctionDepositRefundTaskList() {
        CutPage<TaskList> cutPage = taskListService.queryTaskList(TaskList.CREATE, TaskListType.AUCTION_DEPOSIT_REFUND, 0, 20);
        for (TaskList taskList : cutPage.getiData()) {
            AuctionDeposit deposit = baseRepository.getObjById(AuctionDeposit.class, Long.parseLong(taskList.getObjectId()));
            if (deposit != null) {
                //退款操作
                userAccountService.updateUserAccount(deposit.getUserId(), deposit.getPayMoney(), AccountStatementType.AUCTION_ORDER_REFUND);
                taskListService.updateTaskFinish(taskList);
            }
        }
    }

    //处理竞拍订单退款到余额
    //每天凌晨3点
    @Scheduled(cron = "0 0 3 * * ?")
    public void disposeAuctionOrderRefund() {
        CutPage<TaskList> cutPage = taskListService.queryTaskList(TaskList.CREATE, TaskListType.AUCTION_ORDER_REFUND, 0, CutPage.MAX_COUNT);
        List<TaskList> taskLists = cutPage.getiData();
        List<String> orderIds = taskLists.stream().map(TaskList::getObjectId).collect(Collectors.toList());
        Map<String, AuctionItemOrder> itemMap = baseRepository.queryObjMap(AuctionItemOrder.class, orderIds, "orderId");
        taskLists.forEach(t -> {
            AuctionItemOrder order = itemMap.get(t.getObjectId());
            if (order != null) {
                //退款操作
                order.setStatus(AuctionItemOrderStatus.REFUND_SUCCESS.getCode());
                userAccountService.updateUserAccount(order.getUserId(), order.getPayMoney(), AccountStatementType.AUCTION_ORDER_REFUND);
                taskListService.updateTaskFinish(t);
            }
        });
    }

    //处理新用户注册赠送优惠券
    //每分钟扫描一次
    @Scheduled(cron = "3 * * * * ?")
    public void disposeUserRegisterCoupon() {
        if (DateUtil.isBetweenPeriod(PropertiesConfig.getUserRegisterCouponStartDate(), PropertiesConfig.getUserRegisterCouponEndDate(), new Date())) {
            CutPage<TaskList> cutPage = taskListService.queryTaskList(TaskList.CREATE, TaskListType.REGISTER_COUPON, 0, 20);
            for (TaskList taskList : cutPage.getiData()) {
                //赠送优惠券
                if (StringUtils.isNotBlank(taskList.getObjectId())) {
                    userCouponService.addUserCoupon(taskList.getUserId(), taskList.getObjectId(), 1);
                    taskListService.updateTaskFinish(taskList);
                }
            }
        }
    }

    //竞拍结束处理
    //每小时整点第一秒
    @Scheduled(cron = "1 0/60 * * * ?")
    public void handleAuctionFinish() {
        System.err.println("TimeTaskService.handleAuctionFinish:" + DateFormatUtils.format(new Date(), "HH:mm:ss"));
        List<Auction> auctions = auctionService.queryFinishAuction();
        for (Auction auction : auctions) {
            auctionService.disposeAuctionFinish(auction.getId());
        }
    }

    //竞拍订单到期未评价
    //每天0点刷新
    @Scheduled(cron = "4 0 0 * * ?")
    public void defaultEvaluateAuctionOrder() {
        System.err.println("TimeTaskService.defaultEvaluateAuctionOrder:" + DateFormatUtils.format(new Date(), "HH:mm:ss"));
        List<AuctionItemOrder> auctionItemOrders = auctionService.queryOverdueAuctionOrder(AuctionItemOrderStatus.PAY.getCode(), 24 * EVALUATE_DAY);
        auctionItemOrders.forEach(a -> evaluateSystemService.setOrderEvaluate(a.getOrderId(), true));
    }

    //视频聊天订单到期未评价
    //每天0点刷新
    @Scheduled(cron = "7 0 0 * * ?")
    public void defaultEvaluateVideoChatOrder() {
        List<VideoChatItem> videoChatItems = videoChatService.queryOverdueVideoOrder(VideoChatOrderStatus.PAY.getCode(), 24 * 7);
        videoChatItems.forEach(v -> evaluateSystemService.setOrderEvaluate(v.getOrderId(), true));
    }

    //每分钟扫一次 查出哪些视频聊天已经停止，然后变成空闲
    @Scheduled(cron = "15 * * * * ?")
    public void queryVideoChatForFree() {
        CutPage<VideoChat> videoChatCutPage = videoChatService.queryVideoChatForChatState();
        for (VideoChat videoChat : videoChatCutPage.getiData()) {
            videoChat.setChatState(VideoChatStatus.OFFLINE.getCode());
            baseRepository.updateObj(videoChat);
        }

    }

    private Integer getOpposite(int sex) {
        switch (sex) {
            case 0:
                return 1;
            case 1:
                return 0;
            default:
                return null;
        }
    }

    //赠送优惠券
    private void sendUserCoupon() {
        List<UserVipSetting> list = userVipSettingService.queryUserVipSetting().getiData();
        for (UserVipSetting userVipSetting : list) {
            List<UserVIP> userVIPs = userVIPService.queryVIPListBySettingId(userVipSetting.getId()).getiData();
            for (UserVIP userVIP : userVIPs) {
                userCouponService.addUserCoupon(userVIP.getFkUserId(), userVipSetting.getFkCouponId(), 1);
            }
        }
    }


}
