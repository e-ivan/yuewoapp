package com.war4.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/1/4.
 */
@Component
public  class TimerTaskForUser extends TimerTask {


    protected TimerTaskForUser(){
        super();
    }

    @Override
    @Transactional
    public void run() {

    }
}
