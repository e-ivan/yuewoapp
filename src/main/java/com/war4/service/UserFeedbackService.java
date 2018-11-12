package com.war4.service;

import com.war4.pojo.UserFeedback;

/**
 * 用户反馈服务
 * Created by hh on 2017.8.2 0002.
 */
public interface UserFeedbackService {
    /**
     * 创建反馈信息
     * @param userFeedback
     */
    void createFeedback(UserFeedback userFeedback);
}
