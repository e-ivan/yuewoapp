package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 竞拍押金订单
 * Created by Administrator on 2017/1/13.
 */
@Getter
@AllArgsConstructor
public enum AuctionDepositStatus {
    CREATE(1,"已创建"),
    PAY(2,"已支付"),
    ;

    private Integer code;
    private String value;

    public static AuctionDepositStatus getByCode(Integer code){
        for(AuctionDepositStatus status: AuctionDepositStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
