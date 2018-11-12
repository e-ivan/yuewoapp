package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 简历是否公开
 * Created by Administrator on 2016/12/21.
 */
@Getter
@AllArgsConstructor
public enum  ResumeIsPublic {
    NOTPUBLIC(0,"不公开"),
    PUBLIC(1,"公开");

    private Integer code;
    private String value;
    public static ResumeIsPublic getByCode(Integer code){
        for(ResumeIsPublic status: ResumeIsPublic.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }

}
