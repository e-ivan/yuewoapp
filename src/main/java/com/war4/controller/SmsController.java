package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.BusinessException;
import com.war4.base.PropertiesConfig;
import com.war4.base.Response;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.UserRoleType;
import com.war4.pojo.UserInfoBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dly on 2016/8/9.
 */
@Controller
@RequestMapping(value = "/sms")
public class SmsController extends BaseController{
    //发送手机验证码
    @RequestMapping(value = "sendPhoneValidCode")
    public
    @ResponseBody
    Response sendPhoneValidCode(String phone,boolean register) throws Exception{
        if (register){
            UserInfoBase userBase = userInfoBaseService.getUserByPhoneAndRoleType(phone, UserRoleType.COMMON_USER.getCode());
            if (userBase != null) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "手机号已经注册了！");
            }
        }
        String validCode = tempValidCodeService.createValidCode(phone);
        if (!PropertiesConfig.isProduction()) {
            return new Response(validCode);
        }
        return new Response("短信已发送！");
    }
}
