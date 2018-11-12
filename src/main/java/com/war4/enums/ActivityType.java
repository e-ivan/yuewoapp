package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/7.
 */
@Getter
@AllArgsConstructor
public enum ActivityType {
    ONLINE(10,"线上"),
    OFFLINE(20,"线下"),
    SIGNUP(30,"报名");

    private Integer code;
    private String value;

    public static ActivityType getByCode(Integer code){
        for(ActivityType type: ActivityType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}
