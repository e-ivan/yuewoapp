package com.war4.listener.event;

import com.war4.pojo.ActivityBid;
import org.springframework.context.ApplicationEvent;

/**
 * 投标抽奖活动事件
 * Created by hh on 2017.10.19 0019.
 */
public class ActivityBidLotteryEvent extends ApplicationEvent {
    private ActivityBid activityBid;
    public ActivityBidLotteryEvent(ActivityBid source) {
        super(source);
        this.activityBid = source;
    }

    public ActivityBid getActivityBid() {
        return activityBid;
    }
}
