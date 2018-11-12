package com.war4.enums;

import com.war4.pojo.NearbyDating;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论系统类型
 * Created by E_Iva on 2018.4.11.0011.
 */
@Getter
@AllArgsConstructor
public enum CommentSystemType {
    NEARBY_DATING(2,"附近有约", "NearbyDating", NearbyDating.class);
    public static CommentSystemType getByCode(Integer code){
        for (CommentSystemType type : CommentSystemType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    private Integer code;
    private String value;
    private String clzName;
    private Class clz;
}
