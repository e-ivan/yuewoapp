package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/19.
 */
@Getter
@AllArgsConstructor
public enum DatingStatus {
    CREATE(0,"创建"),
    ACCEPT(10,"接受"),
    CLOSE(20,"结束"),
    SUCCESSFUL(30,"成功");

    public static DatingStatus getByValue(Integer code){
        for(DatingStatus status: DatingStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }

    private Integer code;
    private String value;

}
