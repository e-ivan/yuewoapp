package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商城代金券来源
 * Created by hh on 2017.9.18 0018.
 */
@Getter
@AllArgsConstructor
public enum MarketCouponSource {
    FILM_ORDER(0,"电影订单"),
    CINEMA_RECHARGE(1,"影院充值"),
    ;

    private int code;
    private String sourceName;
}
