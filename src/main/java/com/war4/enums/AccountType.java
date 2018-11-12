package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/13.
 */
@Getter
@AllArgsConstructor
public enum AccountType {
    GOLD("gold","金币"),
    BALANCE("balance","余额");

    private String code;

    private String value;


    public static AccountType getByCode(String code){
        for(AccountType type: AccountType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}
