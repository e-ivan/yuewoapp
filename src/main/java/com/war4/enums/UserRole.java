package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2017/2/8.
 */
@Getter
@AllArgsConstructor
public enum UserRole {
    NORMAL(0,"普通人"),
    INTERNETSTAR(1,"网红");


    private Integer code;
    private String value;

    public static UserRole getByCode(Integer code){
        for(UserRole role: UserRole.values()){
            if (role.getCode().equals(code)){
                return role;
            }
        }
        return null;
    }
}
