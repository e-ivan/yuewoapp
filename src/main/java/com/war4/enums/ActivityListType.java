package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/7.
 */
@Getter
@AllArgsConstructor
public enum ActivityListType {
    OFFLINE("offline","线下"),
    TIME("time","时间");

    private String code;

    private String value;

    public static ActivityListType getByCode(String code){
        for(ActivityListType type: ActivityListType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}
