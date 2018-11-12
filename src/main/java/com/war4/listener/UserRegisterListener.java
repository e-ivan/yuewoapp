package com.war4.listener;

import com.war4.base.PropertiesConfig;
import com.war4.enums.TaskListType;
import com.war4.listener.event.UserRegisterEvent;
import com.war4.pojo.TaskList;
import com.war4.pojo.UserInfoBase;
import com.war4.service.TaskListService;
import com.war4.service.UserInfoBaseService;
import com.war4.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 用户注册监听器
 * Created by hh on 2017.8.12 0012.
 */
@Component
public class UserRegisterListener implements ApplicationListener<UserRegisterEvent> {
    @Autowired
    private UserInfoBaseService userService;
    @Autowired
    private TaskListService taskListService;
    @Async
    @Override
    public void onApplicationEvent(UserRegisterEvent userRegisterEvent) {
        UserInfoBase user = userRegisterEvent.getUser();
        userService.createUserRegister(user.getId());
        //判断是否活动期间
        if (DateUtil.isBetweenPeriod(PropertiesConfig.getUserRegisterCouponStartDate(), PropertiesConfig.getUserRegisterCouponEndDate(),new Date())) {
            taskListService.createTask(new TaskList(user.getId(), PropertiesConfig.getUserRegisterCouponId(), TaskListType.REGISTER_COUPON, null));
        }
    }

}
