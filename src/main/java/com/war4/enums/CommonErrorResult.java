package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dly on 2016/7/29.
 */
@Getter
@AllArgsConstructor
public enum CommonErrorResult {
    BUSINESS_ERROR(100),//前端拿到可以直接弹出
    SECRET_FAIL(500),   //前端拿到不应该直接弹出
    LACK_PAY_PASSWORD(300),//未设置支付密码，需要到修改界面
    BALANCE_DEFICIENCY(305),//余额不足，需要充值
    REEA(200),
    WECHAT_ERROR(400),   //当error为400时，去完善个人信息
    VERIFY_FAIL(600);  //验证身份失败

    private Integer code;
}
