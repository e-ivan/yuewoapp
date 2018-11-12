package com.war4.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;

import java.util.Map;

/**
 * Created by Administrator on 2016/11/4.
 */
public interface PushPayloadService {
    void addAllUserPushPayload(String masterSecret ,String appKey,String msg,String type) throws Exception;
    void addPushPayloadForUserName(String masterSecret ,String appKey,String username, String msg);

    /**
     * 向用户推送消息
     * @param msg
     * @param extras
     * @param deviceIds
     */
    PushResult pushMessage2Alias(String msg, Map<String,String> extras, boolean alert, String... deviceIds) throws APIConnectionException, APIRequestException;
    PushResult pushMessage2RegistrationId(String msg, Map<String,String> extras, boolean alert, String... deviceIds) throws APIConnectionException, APIRequestException;
}
