package com.war4.listener;

import com.war4.listener.event.DatingEvent;
import com.war4.pojo.DatingFilm;
import com.war4.pojo.UserCorrelative;
import com.war4.service.DatingFilmService;
import com.war4.service.UserCorrelativeService;
import com.war4.service.UserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 约会事件监听器
 * Created by hh on 2017.8.30 0030.
 */
@Component
public class DatingListener implements ApplicationListener<DatingEvent> {

    @Autowired
    private UserCorrelativeService correlativeService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private DatingFilmService datingFilmService;


    @Async
    @Override
    @Transactional
    public void onApplicationEvent(DatingEvent datingEvent) {
        //获取事件中的对象
        DatingFilm datingFilm = datingEvent.getDatingFilm();
        //用户约会+1
        correlativeService.addCount(UserCorrelative.DATINGS,datingFilm.getAcceptUserId());
        correlativeService.addCount(UserCorrelative.DATINGS,datingFilm.getCreateUserId());

    }




}
