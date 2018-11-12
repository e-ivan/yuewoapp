package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dly on 2016/9/28.
 */
@Getter
@AllArgsConstructor
public enum MessageFriendApplyStatus {
    CREATE(1,"已发送"),
    ACCEPT(2,"已同意"),
    REFUSE(3,"已拒绝");

    private Integer code;
    private String value;

    public static MessageFriendApplyStatus getByCode(Integer code){
        for(MessageFriendApplyStatus messageFriendApplyStatus: MessageFriendApplyStatus.values()){
            if (messageFriendApplyStatus.getCode().equals(code)){
                return messageFriendApplyStatus;
            }
        }
        return null;
    }
}
