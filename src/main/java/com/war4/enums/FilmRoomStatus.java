package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/30.
 */
@Getter
@AllArgsConstructor
public enum FilmRoomStatus {
    CREATE(0,"创建"),
    PAY_SUCCESS(10,"支付成功！");

    private Integer code;
    private String value;

    public static FilmRoomStatus getByCode(Integer code){
        for(FilmRoomStatus status: FilmRoomStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
