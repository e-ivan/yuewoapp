package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户头像状态
 * Created by dly on 2016/9/23.
 */
@Getter
@AllArgsConstructor
public enum UserHeadStatus {
    FAILED(0,"不通过"),
    PASS(1,"通过"),
    AUDIT(2,"待审核");

    private Integer code;
    private String value;

    public static UserHeadStatus getByCode(Integer code){
        for(UserHeadStatus status: UserHeadStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }

}
