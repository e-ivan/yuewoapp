package com.war4.service;

import com.war4.pojo.UserClientRecord;

/**
 * 用户登录设备信息服务
 * Created by hh on 2017.9.26 0026.
 */
public interface UserClientRecordService {

    /**
     * 保存用户登录设备信息
     * @param userId
     * @param imei
     * @param model
     * @param rid
     */
    void saveUserClient(String userId, String imei, String model, String os, String rid);

    /**
     * 获取用户登录设备
     * @param userId
     * @param imei
     */
    UserClientRecord getUserClient(String userId,String imei);
}
