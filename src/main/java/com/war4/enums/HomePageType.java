package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/13.
 */
@Getter
@AllArgsConstructor
public enum HomePageType {

    CAROUSEL(0,"轮播"),
    POPUP(1,"弹窗"),
    CARANDPOP(2,"弹窗和轮播");
    public static HomePageType getByValue(Integer code){
        for(HomePageType status: HomePageType.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }

    private Integer code;
    private String value;
}
