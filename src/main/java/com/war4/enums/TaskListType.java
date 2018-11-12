package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务列表类型
 * Created by hh on 2017.10.21 0021.
 */
@Getter
@AllArgsConstructor
public enum TaskListType {
    BID_REFUND(0,"bidRefund","投标退款"),
    USER_REGISTER(1,"userRegister","用户注册"),
    REGISTER_COUPON(2,"registerCoupon","注册赠送优惠券"),
    AUCTION_DEPOSIT_REFUND(3,"auctionDepositRefund","竞拍押金退款"),
    FILM_ORDER_REPORT(4,"filmOrderReport","电影数据上报"),
    AUCTION_ORDER_REFUND(5,"auctionOrderRefund","竞拍订单退款"),

    ;

    private Integer code;
    private String value;
    private String typeName;
}
