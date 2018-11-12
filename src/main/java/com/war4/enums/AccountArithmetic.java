package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账户计算算法
 * Created by hh on 2017.10.26 0026.
 */
@Getter
@AllArgsConstructor
public enum AccountArithmetic {
    USABLE_MINUS_FREEZE_ADD(0,false,true,0,"可用减，冻结加"),
    USABLE_ADD_FREEZE_MINUS(1,true,false,0,"可用加，冻结减"),
    USABLE_MINUS(2,false,null,0,"可用减"),
    USABLE_ADD(3,true,null,0,"可用加"),
    FREEZE_MINUS(4,null,false,1,"冻结减"),
    FREEZE_ADD(5,null,true,1,"冻结加");

    private Integer code;
    private Boolean usable;
    private Boolean freeze;
    private Integer amountType;
    private String value;
}
