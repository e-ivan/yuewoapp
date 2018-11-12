package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/13.
 */
@Getter
@AllArgsConstructor
public enum  AccountStatus {

    COST(0,"支出"),
    INCOME(1,"收入");
    public static AccountStatus getByValue(Integer code){
        for(AccountStatus status: AccountStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }

    private Integer code;
    private String value;

}
