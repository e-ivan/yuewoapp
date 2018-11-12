package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 竞拍订单状态
 * Created by Administrator on 2017/1/13.
 */
@Getter
@AllArgsConstructor
public enum AuctionItemOrderStatus {
    CANCEL(0,"已取消"),
    CREATE(1,"已创建"),
    PAY(2,"已支付"),
    EVALUATE(3,"已评价"),
    WAIT_REFUND(4,"待退款"),
    REFUND_SUCCESS(5,"退款成功"),
    ;

    private Integer code;
    private String value;

    public static AuctionItemOrderStatus getByCode(Integer code){
        for(AuctionItemOrderStatus status: AuctionItemOrderStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
