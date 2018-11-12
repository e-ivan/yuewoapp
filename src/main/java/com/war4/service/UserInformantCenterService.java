package com.war4.service;

import com.war4.pojo.UserInformantCenter;

/**
 * Created by hh on 2017.9.8 0008.
 */
public interface UserInformantCenterService {
    /**
     * 创建举报信息
     * @param UserInformantCenter
     */
    void createInformant(UserInformantCenter UserInformantCenter);

    /**
     * 根据举报人和被举报人获取举报信息
     * @param informer  举报人
     * @param defendant 被举报人
     * @param type 类型
     */
    UserInformantCenter queryInformant(String informer,String defendant,int type);

}
