package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2017/1/13.
 */
@Getter
@AllArgsConstructor
public enum FilmOrderStatus {
    CREATE(1,"已创建"),
    CANCEL(2,"已取消"),
    PAY_SUCCESS(3,"支付成功"),
    SUCCESS(4,"购票成功"),
    COMPLETE(6,"已评价"),
    FAILED(5,"购票失败"),
    WAIT_EVALUATE(7,"待评价");

    private Integer code;
    private String value;

    public static FilmOrderStatus getByCode(Integer code){
        for(FilmOrderStatus status: FilmOrderStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
