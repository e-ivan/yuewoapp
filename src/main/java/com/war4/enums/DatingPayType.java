package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by hh on 2017.8.30 0030.
 */
@Getter
@AllArgsConstructor
public enum DatingPayType {
    SENDER(0,"发起人"),
    ACCEPTER(1,"接受人"),
    AA(2,"AA");

    private Integer code;
    private String value;

    public static DatingPayType getByCode(Integer code){
        for(DatingPayType status: DatingPayType.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
