package com.war4.service;

import com.war4.enums.MessageLogType;
import com.war4.pojo.ValidCode;

/**
 * Created by dly on 2016/8/22.
 */
public interface SmsService {
    //发送验证码
    void sendValidCode(ValidCode validCode)  throws Exception;
    //发送名保存信息
    void sendAndSaveSMS(String msg, String mobile, MessageLogType type, String userId, String objectId);
}
