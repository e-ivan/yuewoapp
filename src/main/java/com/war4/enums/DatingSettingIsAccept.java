package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/19.
 */
@Getter
@AllArgsConstructor
public enum DatingSettingIsAccept {
    REFUSE(0,"拒绝"),
    ACCEPT(1,"接受");

    private Integer code;
    private String value;

    public static DatingSettingIsAccept getByCode(Integer code){
        for(DatingSettingIsAccept status: DatingSettingIsAccept.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
