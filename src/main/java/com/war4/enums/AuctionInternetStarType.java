package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 竞拍网络名人类型
 * Created by E_Iva on 2018.3.2.0002.
 */
@Getter@AllArgsConstructor
public enum AuctionInternetStarType {
    INTERNET_STAR(0,"网红"),
    CELEBRITY(1,"名人");
    private Integer code;
    private String value;
    public static AuctionInternetStarType getByCode(Integer code){
        for (AuctionInternetStarType type : AuctionInternetStarType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
