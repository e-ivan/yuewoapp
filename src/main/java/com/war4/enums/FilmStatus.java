package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/27.
 */
@Getter
@AllArgsConstructor
public enum FilmStatus {
    SHELF(0,"下架"),
    SHELVES(1,"上架");

    private Integer code;
    private String value;

    public static FilmStatus getByCode(Integer code){
        for(FilmStatus status: FilmStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
