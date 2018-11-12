package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 宣传页面定位
 * Created by E_Iva on 2018.3.29.0029.
 */
@Getter@AllArgsConstructor
public enum AdPageLocation {

    THIRD_APP(0,"第三方应用"),
    BROWSER(1,"浏览器"),
    APP_MOVIE(2,"电影"),
    APP_ENROLL(3,"报名"),
    APP_NEARBY_DATING(4,"附近有约"),
    APP_AUCTION(5,"竞拍"),
    ;
    public static AdPageLocation getByCode(Integer code){
        for (AdPageLocation location : AdPageLocation.values()) {
            if (location.getCode().equals(code)) {
                return location;
            }
        }
        return null;
    }
    private Integer code;
    private String value;
}
