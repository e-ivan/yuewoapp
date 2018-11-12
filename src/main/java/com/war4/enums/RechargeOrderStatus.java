package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 充值订单状态
 * Created by Administrator on 2016/12/14.
 */
@Getter
@AllArgsConstructor
public enum RechargeOrderStatus {
    CREATE(0,"已创建"),
    PAY(1,"已支付"),
    CANCEL(2,"已取消")
    ;

    private Integer code;
    private String value;

    public static RechargeOrderStatus getByCode(Integer code){
        for(RechargeOrderStatus status: RechargeOrderStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
