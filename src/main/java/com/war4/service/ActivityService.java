package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.Activity;

/**
 * Created by Administrator on 2016/12/7.
 */
public interface ActivityService {
    Activity addActivity(Activity act);
    void deleteActivityById(String activityId);
    CutPage<Activity> queryActivityList(Integer type,Integer page,Integer limit);
}
