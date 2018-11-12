package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 竞拍订单状态
 * Created by Administrator on 2017/1/13.
 */
@Getter
@AllArgsConstructor
public enum VideoChatOrderStatus {

    CREATE(0,"已创建"),
    PAY(1,"已支付"),
    EVALUATE(2,"已评价"),
    ;


    private Integer code;
    private String value;

    public static VideoChatOrderStatus getByCode(Integer code){
        for(VideoChatOrderStatus status: VideoChatOrderStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
