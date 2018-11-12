package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dly on 2016/8/3.
 */
@Getter
@AllArgsConstructor
public enum UserRoleType {
    SUPERMANAGER("superManager"),

    COMMON_USER("commonUser");

    private String code;

    public static UserRoleType getByValue(String code){
        for(UserRoleType roleType: UserRoleType.values()){
            if (roleType.getCode().equals(code)){
                return roleType;
            }
        }
        return null;
    }
}
