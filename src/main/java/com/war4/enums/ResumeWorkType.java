package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/21.
 */
@Getter
@AllArgsConstructor
public enum ResumeWorkType {
    DIRECTOR("director"),
    PERFORMER("performer"),
    SECEENWRITER("seceenwrter");

    private String code;
    public static ResumeWorkType getByValue(String code){
        for(ResumeWorkType type: ResumeWorkType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}
