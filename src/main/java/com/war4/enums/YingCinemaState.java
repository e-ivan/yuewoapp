package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by E_Iva on 2018.2.27.0027.
 */
@Getter@AllArgsConstructor
public enum YingCinemaState {
    CLOSED(0,"关闭"),
    BUSINESS(1,"正常营业"),
    NO_BUSINESS(2,"未营业"),
    ;
    private Integer code;
    private String value;
    public static YingCinemaState getByCode(Integer code){
        for (YingCinemaState state : YingCinemaState.values()) {
            if (state.getCode().equals(code)) {
                return state;
            }
        }
        return null;
    }
}
