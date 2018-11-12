package com.war4.listener.event;

import com.war4.pojo.ActivityBid;
import org.springframework.context.ApplicationEvent;

/**
 * 投标活动事件
 * Created by hh on 2017.10.19 0019.
 */
public class ActivityBidBeginEvent extends ApplicationEvent {
    private ActivityBid activityBid;
    public ActivityBidBeginEvent(ActivityBid source) {
        super(source);
        this.activityBid = source;
    }

    public ActivityBid getActivityBid() {
        return activityBid;
    }
}
