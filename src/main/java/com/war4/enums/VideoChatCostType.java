package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单支付方式
 * Created by hh on 2017.10.25 0025.
 */
@Getter
@AllArgsConstructor
public enum VideoChatCostType {
    ONESTEP(0,"第一段扣费标准");

    private Integer code;
    private String value;

}
