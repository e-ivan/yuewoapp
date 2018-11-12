package com.war4.service.impl;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.ApacheHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.war4.base.BusinessException;
import com.war4.base.PropertiesConfig;
import com.war4.enums.CommonErrorResult;
import com.war4.service.PushPayloadService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/4.
 */
@Service
public class PushPayloadServiceImpl implements PushPayloadService {

    private static final String AUDIENCE_ALIAS = "alias";
    private static final String AUDIENCE_REGISTRATION_ID = "registrationId";

    private static final String JPUSH_APP_MASTER_SECRET = PropertiesConfig.getJpushAppMasterSecret();
    private static final String JPUSH_APPKEY = PropertiesConfig.getJpushAppkey();

    @Override
    @Deprecated
    public void addAllUserPushPayload(String masterSecret, String appKey, String msg, String type) throws Exception {
        JPushClient jpushClient = new JPushClient(masterSecret, appKey);
        String authCode = ServiceHelper.getBasicAuthorization(appKey, masterSecret);
        jpushClient.getPushClient().setHttpClient(new ApacheHttpClient(authCode, null, ClientConfig.getInstance()));
        try {
            jpushClient.sendPush(buildPushObject_all_all_alert(msg));
        } catch (APIConnectionException e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络错误");
        } catch (APIRequestException e) {
            System.out.println("Error response from JPush server. Should review and fix it. " + e);
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Msg ID: " + e.getMsgId());
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "未知错误");
        }
    }

    @Override
    @Deprecated
    public void addPushPayloadForUserName(String masterSecret, String appKey, String username, String msg) {
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, true, 0);
        try {
            jpushClient.sendPush(buildPushObject_ios_audienceMore_messageWithExtras(username, msg));
        } catch (APIConnectionException e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络错误");
        } catch (APIRequestException e) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "未知错误");
        }
    }

    @Override
    public PushResult pushMessage2Alias(String msg, Map<String, String> extras, boolean alert, String... deviceIds) throws APIConnectionException, APIRequestException {
        return baseSendPush(msg, extras, alert, AUDIENCE_ALIAS, deviceIds);
    }

    @Override
    public PushResult pushMessage2RegistrationId(String msg, Map<String, String> extras, boolean alert, String... deviceIds) throws APIConnectionException, APIRequestException {
        return baseSendPush(msg, extras, alert, AUDIENCE_REGISTRATION_ID, deviceIds);
    }

    private PushResult baseSendPush(String msg, Map<String, String> extras, boolean alert, String audience, String... deviceIds) throws APIConnectionException, APIRequestException {
        JPushClient jpushClient = new JPushClient(JPUSH_APP_MASTER_SECRET, JPUSH_APPKEY);
        String authCode = ServiceHelper.getBasicAuthorization(JPUSH_APPKEY, JPUSH_APP_MASTER_SECRET);
        jpushClient.getPushClient().setHttpClient(new ApacheHttpClient(authCode, null, ClientConfig.getInstance()));
        System.out.println("deviceIds = " + Arrays.toString(deviceIds));
        System.out.println("-----------------推送消息------------------");
        PushResult pushResult = jpushClient.sendPush(buildPushObject_all_alias_message_alert(msg, extras, alert, audience, deviceIds));
        System.out.println("pushResult = " + pushResult);
        return pushResult;
    }

    public static void main(String[] args) throws Exception {
        //教师
        //  JPushTest.testSendPush("d070a6cf2c9408227a4322ae","9bdf15b66c1bac091cd9f05a");
        //机构
//        JPushTest.testSendPush("d593e0d46ce9ec0ee7ede3be","24ef65cae9b47668d91b67b4");
        //JPushClient jpushClient = new JPushClient("aec93e195074da4637163a92", "bc7fd6e7aa471b04d0faea53", true, 5);

        // System.out.println(new PushPayloadServiceImpl().pushAlertSomeUser("继续哈哈", "149863926946734683"));
//        PushResult result = jpushClient.sendPush(buildPushObject_ios_audienceMore_messageWithExtras("149863926946734683", "这是指定用户的推送"));
//            PushResult result = jpushClient.sendPush(buildPushObject_all_all_alert("约你看电影"));
//            System.out.println(result);
    }

    private static PushPayload buildPushObject_all_all_alert(String msg) {
        return PushPayload.alertAll(msg);
    }


    private static PushPayload buildPushObject_all_alias_message_alert(String msg, Map<String, String> extras, boolean alert, String audience, String... deviceIds) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(switchAudience(audience, deviceIds))//Audience设置为all，说明采用广播方式推送，所有用户都可以接收到
                .setNotification(alert ?
                        (Notification.newBuilder()
                                .addPlatformNotification(IosNotification.newBuilder().setAlert(msg).addExtras(extras).build())
                                .addPlatformNotification(AndroidNotification.newBuilder().setAlert(msg).addExtras(extras).build())
                                .build())
                        : null)
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg)
                        .setContentType(extras.get("type"))
                        .addExtras(extras)
                        .build())
                .setOptions(Options.newBuilder().setApnsProduction(PropertiesConfig.isProduction()).build())
                .build();
    }

    private static Audience switchAudience(String audience, String... deviceIds) {
        switch (audience) {
            case AUDIENCE_ALIAS:
                return Audience.alias(deviceIds);
            case AUDIENCE_REGISTRATION_ID:
                return Audience.registrationId(deviceIds);
            default:
                return Audience.all();
        }
    }


    private static PushPayload buildPushObject_ios_audienceMore_messageWithExtras(String userId, String msg) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(userId))
                .setNotification(Notification.alert(msg))
                .build();
    }


}
