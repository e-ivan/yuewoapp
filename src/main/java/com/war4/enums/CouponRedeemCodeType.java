package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优惠券兑换类型
 * Created by E_Iva on 2018.4.16.0016.
 */
@Getter@AllArgsConstructor
public enum CouponRedeemCodeType {
    FILM(0,"film"),
    MARKET(1,"market");
    public static CouponRedeemCodeType getByValue(String value){
        for (CouponRedeemCodeType type : CouponRedeemCodeType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
    private Integer code;
    private String value;
}
