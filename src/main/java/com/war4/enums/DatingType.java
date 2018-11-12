package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2017/1/11.
 */
@Getter
@AllArgsConstructor
public enum DatingType {

    CREATE(0,"我创建的"),

    ACCPET(1,"我接受的");

    public static DatingType getByValue(Integer code){
        for(DatingType type: DatingType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }

    private Integer code;
    private String value;
}
