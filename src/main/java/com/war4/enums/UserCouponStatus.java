package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2017/1/17.
 */
@Getter
@AllArgsConstructor
public enum UserCouponStatus {
    NOT_USE(0,"未使用"),
    USED(1,"已使用");

    private Integer code;
    private String value;

    public static UserCouponStatus getByCode(Integer code){
        for(UserCouponStatus status: UserCouponStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
