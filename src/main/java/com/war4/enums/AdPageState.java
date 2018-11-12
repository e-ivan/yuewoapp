package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 宣传页面状态
 * Created by E_Iva on 2018.3.29.0029.
 */
@Getter@AllArgsConstructor
public enum AdPageState {

    NO_SHOW(0,"不显示"),
    SHOW(1,"显示"),
    ;

    private Integer code;
    private String value;
}
