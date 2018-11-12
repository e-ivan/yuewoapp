package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提现账户类型
 * Created by hh on 2017.11.6 0006.
 */
@Getter
@AllArgsConstructor
public enum CashAccountType {
    ALIPAY(0,"支付宝"),
    WECHAT(1,"微信"),
    BANK(2,"普通银行");

    private Integer code;
    private String value;
    public static CashAccountType getByCode(Integer code){
        for(CashAccountType type: CashAccountType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }

}
