package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.ActivityType;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.Activity;
import com.war4.repository.BaseRepository;
import com.war4.service.ActivityService;
import com.war4.service.AssembleService;
import com.war4.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by Administrator on 2016/12/7.
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public Activity addActivity(Activity act) {
        Activity activity = new Activity();
        activity.setId(UUID.randomUUID().toString());
        activity.setTitle(act.getTitle());
        activity.setActivityUrl(act.getActivityUrl().trim());
        activity.setType(act.getType());
        activity.setIntro(act.getIntro());
        activity.setOutUrl(act.getOutUrl().trim());
        activity.setDetail(act.getDetail());
        activity.setStartTime(act.getStartTime());
        activity.setEndTime(act.getEndTime());

        baseRepository.saveObj(activity);
        return activity;
    }

    @Override
    @Transactional
    public void deleteActivityById(String activityId) {
        Activity activity = baseRepository.getObjById(Activity.class,activityId);
        if(activity==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "您要删除的活动不存在!");
        }
        baseRepository.logicDelete(activity);
    }

    @Override
    public CutPage<Activity> queryActivityList(Integer type, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(Activity.class);


        if(Objects.equals(type, ActivityType.OFFLINE.getCode())){
                hql +=" and type ='"+type+"'";
        }
        if(Objects.equals(type, ActivityType.ONLINE.getCode())){
                hql +=" and type ='"+type+"'";

        }
        CutPage<Activity> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(Activity activity :cutPage.getiData()){
            activity = assembleService.assembleObject(activity);

            if (activity.getEndTime() != null){
                Date now = new Date();
                int flag = DateUtil.compareDate(now,activity.getEndTime());
                if(flag == -1){
                    activity.setEndFlag(0);
                }
                else {
                    activity.setEndFlag(1);
                }
            }

        }
        return cutPage;
    }
}
