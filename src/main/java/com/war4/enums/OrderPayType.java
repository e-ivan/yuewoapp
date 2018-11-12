package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单支付方式
 * Created by hh on 2017.10.25 0025.
 */
@Getter
@AllArgsConstructor
public enum OrderPayType {
    ALIPAY(0,"alipay","支付宝"),
    WXPAY(1,"wxpay","微信"),
    COUPON(2,"coupon","优惠券"),
    BALANCE(3,"balance","余额"),
    SALE(4,"sale","优惠")
    ;
    public static OrderPayType getByCode(Integer code){
        for (OrderPayType type : OrderPayType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    private Integer code;
    private String value;
    private String typeName;
}
