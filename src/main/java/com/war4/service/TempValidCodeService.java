package com.war4.service;

import java.awt.image.BufferedImage;

/**
 * Created by dly on 2016/8/22.
 */
public interface TempValidCodeService {
    //发送验证码
    String createValidCode(String phone) throws Exception;
    //校验验证码
    void checkValidCode(String phone, String validCode);
    //创建验证码和图片
    BufferedImage createCaptcha(String userId);
    //校验图片验证码
    void checkCaptchaCode(String userId,String captchaCode);
}
