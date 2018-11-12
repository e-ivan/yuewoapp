package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/14.
 */
@Getter
@AllArgsConstructor
public enum RechargeStatus {
    CREATE(10,"已创建"),
    PAY_SUCCESS(20,"支付成功"),
    ORDER_OVER(50,"成功");

    private Integer code;
    private String value;

    public static RechargeStatus getByCode(Integer code){
        for(RechargeStatus status: RechargeStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
