package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dly on 2016/9/23.
 */
@Getter
@AllArgsConstructor
public enum  UserStatus {
    PENDING(10,"审核中"),
    NORMAL(20,"正常"),
    STOP(30,"停用");

    private Integer code;
    private String value;

    public static UserStatus getByCode(Integer code){
        for(UserStatus status: UserStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }

}
