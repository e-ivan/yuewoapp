package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 宣传页面类型变量
 * Created by E_Iva on 2018.3.29.0029.
 */
@Getter@AllArgsConstructor
public enum AdPageTypeVar {
    AD_HOME_BANNER(0,1),//首页轮播
    AD_HOME_POP(1,1 << 1), //首页弹窗
    AD_AUCTION_BANNER(2,1 << 2),//竞拍轮播
    ;
    private Integer code;
    private Integer typeValue;

    public static AdPageTypeVar getByCode(Integer code){
        for (AdPageTypeVar var : AdPageTypeVar.values()) {
            if (var.getCode().equals(code)) {
                return var;
            }
        }
        return null;
    }

}
