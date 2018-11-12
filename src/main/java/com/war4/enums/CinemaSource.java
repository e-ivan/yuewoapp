package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电影影院来源平台
 * Created by E_Iva on 2018.2.27.0027.
 */
@Getter@AllArgsConstructor
public enum CinemaSource {
    TIANZHI(0,"t","天智创客"),
    KOU_DIAN_YING(1,"a","抠电影"),
    ;
    private Integer code;
    private String value;
    private String sourceName;
    public static CinemaSource getByCode(Integer code){
        for (CinemaSource source : CinemaSource.values()) {
            if (source.getCode().equals(code)) {
                return source;
            }
        }
        return null;
    }

}
