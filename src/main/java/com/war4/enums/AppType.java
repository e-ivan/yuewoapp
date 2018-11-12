package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户端设备类型
 * Created by E_Iva on 2018.1.17.0017.
 */
@Getter@AllArgsConstructor
public enum AppType {
    ANDROID_PHONE((byte) 1,"安卓手机"),
    ANDROID_PAD((byte) 2,"安卓平板"),
    IOS_PHONE((byte) 3,"苹果手机"),
    IOS_PAD((byte) 4,"苹果平板"),
    ;
    private Byte code;
    private String typeName;
}
