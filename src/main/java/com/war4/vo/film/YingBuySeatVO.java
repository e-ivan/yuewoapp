package com.war4.vo.film;

import lombok.Getter;
import lombok.Setter;

/**
 * 鼎新锁定座位后购买中的seat信息
 * Created by hh on 2017.9.23 0023.
 */
@Getter
@Setter
public class YingBuySeatVO {
    private String seatId;          //座位id
    private Double handleFee;       //单张票总手续费（统一）
    private Double price;           //不包含手续费的影票价格（含服务费，即service_fee对应的值）
    private Double useRealPayPrice; //用户真实支付影票金额，不含手续费、服务费
    private Double serviceFee;      //影厅座位服务费
}
