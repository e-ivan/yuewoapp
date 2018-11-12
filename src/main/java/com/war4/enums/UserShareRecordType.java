package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户分享类型
 * Created by E_Iva on 2018.1.4.0004.
 */
@Getter
@AllArgsConstructor
public enum UserShareRecordType {
    BANNER(0,"首页轮播图"),
    ARTICLE(1,"文章分享"),
    ;
    public static UserShareRecordType getByCode(Integer code){
        for(UserShareRecordType type: UserShareRecordType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }


    private Integer code;
    private String value;
}
