package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收藏类型
 * Created by hh on 2017.7.14 0014.
 */
@Getter
@AllArgsConstructor
public enum CollectType {
    ARTICLE(0,"article","文章收藏"),
    FILM(1,"film","电影收藏");


    private int code;
    private String value;
    private String typeName;
}
