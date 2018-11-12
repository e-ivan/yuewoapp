package com.war4.listener.event;

import com.war4.pojo.UserViewHistory;
import org.springframework.context.ApplicationEvent;

/**
 * 浏览事件
 * Created by hh on 2017.7.25 0025.
 */
public class ViewHistoryEvent extends ApplicationEvent{
    private UserViewHistory userViewHistory;

    public ViewHistoryEvent(UserViewHistory source) {
        super(source);
        this.userViewHistory = source;
    }

    public UserViewHistory getUserViewHistory() {
        return userViewHistory;
    }
}
