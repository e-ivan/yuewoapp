package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/23.
 */
@Getter
@AllArgsConstructor
public enum RecruitType {
    ACOTER("actor"),
    DIRECTOR("director"),
    SECEENWRITER("seceenwriter");

    public static RecruitType getByValue(String code){
        for(RecruitType status: RecruitType.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }

    private String code;
}
