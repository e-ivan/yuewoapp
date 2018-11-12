package com.war4.service.impl;

import com.war4.enums.MessageLogType;
import com.war4.pojo.UserSms;
import com.war4.pojo.ValidCode;
import com.war4.repository.BaseRepository;
import com.war4.service.SmsService;
import com.war4.util.SmsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by dly on 2016/8/22.
 */
@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private BaseRepository baseRepository;
    @Override
    //发送验证码
    public void sendValidCode(ValidCode validCode) throws Exception{
        String msg = "您的验证码是: " + validCode.getValidCode() + ",请在 "+ ValidCode.VALID_MINUTE +" 分钟内使用";
        SmsUtil.sendSMS(validCode.getPhone(),msg);
    }

    @Override
    @Transactional
    public void sendAndSaveSMS(String msg, String mobile, MessageLogType type, String userId, String objectId) {
        if (StringUtils.isBlank(mobile)) {
            return;
        }
        //保存用户短信信息
        UserSms us = new UserSms();
        us.setSendCount(1);
        us.setContent(msg);
        us.setType(type.getCode());
        us.setUserId(userId);
        us.setObjId(objectId);
        us.setMobile(mobile);
        //发送短信
        try {
            SmsUtil.sendSMS(mobile,msg);
            us.setState(UserSms.SEUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            us.setState(UserSms.FAILED);
        }
        us.setSendTime(new Date());
        us.setCreated(new Date());
        baseRepository.saveObj(us);
    }
}
