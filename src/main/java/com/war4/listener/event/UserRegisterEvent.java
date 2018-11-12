package com.war4.listener.event;

import com.war4.pojo.UserInfoBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

/**
 * 用户注册事件
 * Created by hh on 2017.8.12 0012.
 */
public class UserRegisterEvent extends ApplicationEvent{
    @Autowired
    private UserInfoBase user;

    public UserRegisterEvent(UserInfoBase user) {
        super(user);
        this.user = user;
    }

    public UserInfoBase getUser(){
        return this.user;
    }
}
