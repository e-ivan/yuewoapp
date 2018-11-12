package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论系统状态
 * Created by E_Iva on 2018.4.11.0011.
 */
@Getter@AllArgsConstructor
public enum CommentSystemState {
    NORMAL(1,"正常"),
    DELETE(0,"删除");

    private Integer code;
    private String value;
}
