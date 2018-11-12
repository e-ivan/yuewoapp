package com.war4.listener;

import com.war4.listener.event.ActivityBidLotteryEvent;
import com.war4.pojo.ActivityBid;
import com.war4.service.ActivityBidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 投标活动开始事件
 * Created by hh on 2017.10.19 0019.
 */
@Component
public class ActivityBidLotteryListener implements ApplicationListener<ActivityBidLotteryEvent> {
    @Autowired
    private ActivityBidService activityBidService;

    @Override
    @Async
    public void onApplicationEvent(ActivityBidLotteryEvent activityBidLotteryEvent) {
        ActivityBid activityBid = activityBidLotteryEvent.getActivityBid();
        activityBidService.handleLotteryDrawing(activityBid.getId());
    }
}
