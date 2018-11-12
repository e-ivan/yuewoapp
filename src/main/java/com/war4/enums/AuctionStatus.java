package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 竞拍状态
 * Created by hh on 2017.10.30 0030.
 */
@Getter
@AllArgsConstructor
public enum AuctionStatus {
    FINISH(2,"已结束"),
    CANCEL(3,"已取消"),
    EVALUATE(4,"已评价")
    ;
    private Integer code;
    private String value;
}
