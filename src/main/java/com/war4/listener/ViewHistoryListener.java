package com.war4.listener;

import com.war4.listener.event.ViewHistoryEvent;
import com.war4.pojo.UserCorrelative;
import com.war4.pojo.UserViewHistory;
import com.war4.service.UserCorrelativeService;
import com.war4.service.UserViewHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 浏览历史监听器
 * Created by hh on 2017.7.25 0025.
 */
@Component
public class ViewHistoryListener implements ApplicationListener<ViewHistoryEvent>{
    @Autowired
    private UserViewHistoryService historyService;

    @Autowired
    private UserCorrelativeService correlativeService;
    @Async
    @Override
    public void onApplicationEvent(ViewHistoryEvent viewHistoryEvent) {
        UserViewHistory userViewHistory = viewHistoryEvent.getUserViewHistory();
        int i = historyService.addViewHistory(userViewHistory);
        if (i <= 1){
            //第一次浏览则看过+1
            correlativeService.addCount(UserCorrelative.VIEWS,userViewHistory.getUserId());
        }
    }
}
