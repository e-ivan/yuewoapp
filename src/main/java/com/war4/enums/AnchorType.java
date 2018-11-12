package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 直播类别
 * Created by Administrator on 2016/12/28.
 */
@Getter
@AllArgsConstructor
public enum AnchorType {

    NEAR("near"),
    ATTENTION("attention"),
    HOST("host");

    public static AnchorType getByValue(String code){
        for(AnchorType type: AnchorType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }

    private String code;
}
