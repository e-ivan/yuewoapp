package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商城订单状态
 * Created by hh on 2017.9.24 0024.
 */
@Getter
@AllArgsConstructor
public enum MarketOrderStatus {
    WAIT(0,"待付款"),
    PAY(1,"已付款"),
    CONFIRM_GOODS(2,"待发货"),
    END(3,"已完成"),
    CANCEL(4,"已取消"),
    DELIVERED(5,"已发货");

    private Integer code;
    private String value;
    public static MarketOrderStatus getByCode(Integer code){
        for(MarketOrderStatus status: MarketOrderStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
