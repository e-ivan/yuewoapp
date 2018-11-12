package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 附近有约状态
 * Created by E_Iva on 2018.3.23.0023.
 */
@Getter@AllArgsConstructor
public enum NearbyDatingState {
    CANCEL(0,"已取消"),
    NORMAL(1,"正常");
    private Integer code;
    private String value;
}
