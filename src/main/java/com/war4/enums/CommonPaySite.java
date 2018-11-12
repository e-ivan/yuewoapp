package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用支付方
 * Created by E_Iva on 2018.3.9.0009.
 */
@Getter@AllArgsConstructor
public enum CommonPaySite {
    ACTOR(0,"参与方"),
    CREATOR(1,"发起方"),
    AA(2,"AA"),
    ;
    private Integer code;
    private String value;
}
