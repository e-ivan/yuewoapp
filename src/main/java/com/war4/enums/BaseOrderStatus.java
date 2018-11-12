package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一订单状态
 * Created by hh on 2017.9.24 0024.
 */
@Getter
@AllArgsConstructor
public enum BaseOrderStatus {
    CREATE(0,"已创建"),
    PAY(1,"已付款"),
    EVALUATE(2,"已评价");

    private Integer code;
    private String value;
    public static BaseOrderStatus getByCode(Integer code){
        for(BaseOrderStatus status: BaseOrderStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
