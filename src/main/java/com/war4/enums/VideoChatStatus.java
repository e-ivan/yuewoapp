package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/13.
 */
@Getter
@AllArgsConstructor
public enum VideoChatStatus {
    OFFLINE(0,"空闲中"),
    ONLINE(1,"视频中");
    private Integer code;

    private String value;

    public static VideoChatStatus getByCode(String code){
        for(VideoChatStatus type: VideoChatStatus.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}
