package com.war4.util;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPushTest {
    protected static final Logger LOG = LoggerFactory.getLogger(JPushTest.class);

    // demo App defined in resources/jpush-api.conf

    public static final String TITLE = "申通快递";
    public static final String ALERT = "祝大家新春快乐";
    public static final String MSG_CONTENT = "申通快递祝新老客户新春快乐";
    public static final String REGISTRATION_ID = "0900e8d85ef";
    public static final String TAG = "tag_api";

    public  static JPushClient jpushClient=null;

    public static void main(String[] args) {
        testSendPush("bc7fd6e7aa471b04d0faea53","aec93e195074da4637163a92");
    }

    public static void testSendPush(String appKey ,String masterSecret) {



        jpushClient = new JPushClient(masterSecret, appKey);

        // HttpProxy proxy = new HttpProxy("localhost", 3128);
        // Can use this https proxy: https://github.com/Exa-Networks/exaproxy


        // For push, all you need do is to build PushPayload object.
        PushPayload payload = buildPushObject_android_and_ios();
        //生成推送的内容，这里我们先测试全部推送
//        PushPayload payload = buildPushObject_all_all_alert();


        try {

            System.out.println("开始推送"+payload.toString());
            PushResult result = jpushClient.sendPush(payload);
            System.out.println("推送结果"+result+"................................");

            System.out.println("Got result - " + result);

        } catch (APIConnectionException e) {
            System.out.println("Connection error. Should retry later. "+ e);
            LOG.error("Connection error. Should retry later. ", e);

        } catch (APIRequestException e) {


            System.out.println("Error response from JPush server. Should review and fix it. "+ e);
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Msg ID: " + e.getMsgId());
        }
    }

    public static PushPayload buildPushObject_all_all_alert() {
        return PushPayload.alertAll(ALERT);
    }


    public static PushPayload buildPushObject_all_alias_alert() {

        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.tag("messageTypeOrg"))//Audience设置为all，说明采用广播方式推送，所有用户都可以接收到
                .setMessage(Message.newBuilder()
                        .setMsgContent(MSG_CONTENT)
                        .addExtra("from", "JPush")
                        .build())
                .build();
    }
//    public static PushPayload buildPushObject_all_alias_alert() {
//        return PushPayload.newBuilder()
//                .setPlatform(Platform.all())//设置接受的平台
//                .setAudience(Audience.tag_and("messageTypeOrg"))//Audience设置为all，说明采用广播方式推送，所有用户都可以接收到
//                .setNotification(Notification.alert(ALERT))
//                .build();
//    }

    public static PushPayload buildPushObject_android_tag_alertWithTitle() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.all())
                .setNotification(Notification.android(ALERT, TITLE, null))
                .build();
    }

    public static PushPayload buildPushObject_android_and_ios() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(""))

                .setNotification(Notification.newBuilder()
                        .setAlert("alert content")
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle("Android Title").build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtra("extra_key", "extra_value").build())
                        .build())
                .build();
    }

    public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.tag_and("tag1", "tag_all"))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(ALERT)
                                .setBadge(5)
                                .setSound("happy")
                                .addExtra("from", "JPush")
                                .build())
                        .build())
                .setMessage(Message.content(MSG_CONTENT))
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .build())
                .build();
    }

    public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.alias("messageTypeOrg"))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(MSG_CONTENT)
                        .addExtra("from", "JPush")
                        .build())
                .build();
    }
}