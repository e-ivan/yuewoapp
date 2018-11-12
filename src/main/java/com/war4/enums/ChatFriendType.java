package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/10/26.
 */
@Getter
@AllArgsConstructor
public enum ChatFriendType {
    FRIEND(1,"朋友"),
    STRANGER(2,"陌生人"),
    USERBLACK(3,"黑名单");

    private Integer code;
    private String value;

    public static ChatFriendType getByValue(Integer code){
        for(ChatFriendType type: ChatFriendType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}
