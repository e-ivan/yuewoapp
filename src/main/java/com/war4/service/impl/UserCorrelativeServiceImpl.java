package com.war4.service.impl;

import com.war4.pojo.UserCorrelative;
import com.war4.repository.BaseRepository;
import com.war4.service.UserCorrelativeService;
import com.war4.service.UserRatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.7.25 0025.
 */
@Service
public class UserCorrelativeServiceImpl implements UserCorrelativeService {
    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private UserRatioService userRatioService;

    @Override
    public UserCorrelative createUserCorrelative(String userId) {
        UserCorrelative uc = new UserCorrelative();
        uc.setUserId(userId);
        baseRepository.saveObj(uc);
        return uc;
    }

    @Override
    public void addCount(String field, String userId) {
        addCount(field, userId, 1);
    }

    @Override
    public void addCount(String field, String userId, int num) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        baseRepository.executeHql(createDisposeCountHql(field,num,true), map);
    }

    @Override
    public void reduceCount(String field, String userId) {
        reduceCount(field, userId, 1);
    }

    @Override
    public void reduceCount(String field, String userId, int num) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        baseRepository.executeHql(createDisposeCountHql(field,num,false), map);
    }

    /**
     * 创建处理数量hql
     * @param add true增加、false减少
     */
    private static StringBuilder createDisposeCountHql(String field, int num, boolean add){
        StringBuilder hql = new StringBuilder(50);
        return hql.append("update UserCorrelative set ").append(field).append(" = ").append(field).append(add ? " + " : " - ")
                .append(num < 0 ? 0 : num).append(" where userId = :userId");
    }

    @Override
    public UserCorrelative queryUserCorrelativeByUserId(String userId) {
        String hql = "from UserCorrelative where userId = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        UserCorrelative userCorrelative = baseRepository.executeHql(hql, map);
        if (userCorrelative != null) {
            userCorrelative.setRatio(userRatioService.getLatestByUser(userId));
        }
        return userCorrelative;
    }
}
