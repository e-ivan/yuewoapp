package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/10/19.
 */
@Getter
@AllArgsConstructor
public enum CommonWhether {
    FALSE(0,"否"),
    TRUE(1,"是");
    public static CommonWhether getByValue(Integer code){
        for(CommonWhether sort: CommonWhether.values()){
            if (sort.getCode().equals(code)){
                return sort;
            }
        }
        return null;
    }

    private Integer code;
    private String value;
}
