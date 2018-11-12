package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 影厅座位类型
 * Created by hh on 2017.9.4 0004.
 */
@Getter
@AllArgsConstructor
public enum HallSeatType {
    GENERAL(0,"danren","普通座"),
    DOUBLE(1,"shuangren","双人座"),
    VIP(2,"vip","VIP座"),
    SHAKE(3,"zhendong","震动座"),
    RESERVE(4,"baoliu","保留座"),
    DISABILITY(5,"canji","残疾座"),
    ROAD(6,"road","过道");

    public static int getCodeByType(String type){
        for (HallSeatType hallSeatType : HallSeatType.values()) {
            if (hallSeatType.getType().equals(type)) {
                return hallSeatType.getCode();
            }
        }
        return -1;
    }

    private int code;
    private String type;
    private String typeName;
}
