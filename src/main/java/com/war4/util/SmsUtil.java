package com.war4.util;

import com.war4.thirdParty.smsTools.SmsClientSend;
import org.springframework.stereotype.Component;

/**
 * Created by dly on 2016/8/22.
 */
@Component
public class SmsUtil {
    public static void sendSMS(String phone,String msg) throws Exception {
        String url = "http://client.movek.net:8888/sms.aspx";
        String content = msg+"【约我】";
        String userId = "289";
        String userName = "WEB-A289-289";
        String password = "579634";
        SmsClientSend.sendSms(url, userId, userName, password, phone, content);
    }
}
