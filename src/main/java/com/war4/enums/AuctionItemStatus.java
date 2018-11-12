package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 竞拍出价明细状态
 * Created by hh on 2017.11.1 0001.
 */
@Getter
@AllArgsConstructor
public enum AuctionItemStatus {
    OUT(0,"出局"),
    FIRST(1,"领先"),
    WAIT(2,"待付款"),
    PAY(3,"已付款"),
    ;

    private Integer code;
    private String value;
}
