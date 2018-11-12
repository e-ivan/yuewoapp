package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dly on 2016/8/15.
 */
@Getter
@AllArgsConstructor
public enum CommonDeleteFlag {
    DELETED(1),       //已删除
    NOT_DELETED(0);  //未删除
    private Integer code;
}
