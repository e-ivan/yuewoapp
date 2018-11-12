package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.ActivityBid;
import com.war4.pojo.ActivityBidItem;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/7.
 */
public interface ActivityBidService {
    ActivityBid addActivityBid(ActivityBid activityBid);

    void deleteActivityBid(Long bId);

    void updateActivityBidState(Long id,Byte state);

    CutPage<ActivityBid>queryActivityBidList(Byte type,Integer state,Integer page,Integer limit) ;

    CutPage<ActivityBid>queryActivityBidListByDate(Byte type,Byte state,Date date,Integer page,Integer limit) ;

    CutPage<ActivityBid>queryActivityBidListByBeginToLottery(Byte type,Integer state,Date date,Integer page,Integer limit) ;

    ActivityBid getActivityBid(Long bId,boolean correlation,String userId);
    ActivityBid getActivityBid(Long bId);

    String createActivityBidItem(Long bId,String userId,Integer count);

    /**
     * 连续参加活动
     */
    void continuousJoinActivityBid(Long bId);

    CutPage<ActivityBidItem> queryActivityBidItemList(Long bId, String userId, Boolean action, Integer page, Integer limit);

    CutPage<ActivityBidItem> queryActivityBidItemList(Long bId, Byte state, Integer page, Integer limit);

    ActivityBidItem getActivityBidItemByOrderId(String orderId);

    /**
     * 获取参与下一期的明细
     */
    CutPage<ActivityBidItem> queryNextActivityBidItem(Integer page,Integer limit);

    /**
     * 明细付款超时处理
     */
    void handleItemTimeout(Integer minute);

    void handleLotteryDrawing(Long bId);
//    void handNoLottery(ActivityBidItem activityBidItem);

    void setOrderPaySuccess(String orderId);
}
