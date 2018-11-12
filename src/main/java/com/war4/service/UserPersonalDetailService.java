package com.war4.service;

import com.war4.pojo.UserPersonalDetail;

/**
 * 用户个人信息服务
 * Created by hh on 2017.8.30 0030.
 */
public interface UserPersonalDetailService {
    void createUserPersonalDetail(String userId);

    /**
     * 更新用户个人信息
     * @param userPersonalDetail
     */
    UserPersonalDetail updateUserPersonalDetail(UserPersonalDetail userPersonalDetail);

    UserPersonalDetail getUserPersonalDetail(String userId) throws Exception;

    //设置红人约会印象
    void setUserDateImpression(String userId);

}
