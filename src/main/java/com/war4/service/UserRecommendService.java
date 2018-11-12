package com.war4.service;

import com.war4.pojo.UserRecommend;

/**
 * 用户推荐服务
 * Created by hh on 2017.8.7 0007.
 */
public interface UserRecommendService {
    /**
     * 添加推荐人
     * @param userId    注册用户id
     * @param recommendCode  推荐码
     */
    void addReferrer(String userId,String recommendCode);

    /**
     * 根据用户id获取用户推荐
     * @param userId
     */
    UserRecommend getRecommendByUser(String userId);

}
