package com.war4.service;

import com.war4.pojo.UserCorrelative;

/**
 * 用户相关服务
 * Created by hh on 2017.7.25 0025.
 */
public interface UserCorrelativeService {
    UserCorrelative createUserCorrelative(String userId);
    void addCount(String field,String userId);
    void addCount(String field,String userId,int num);
    void reduceCount(String field,String userId);
    void reduceCount(String field,String userId,int num);
    UserCorrelative queryUserCorrelativeByUserId(String userId);
}
