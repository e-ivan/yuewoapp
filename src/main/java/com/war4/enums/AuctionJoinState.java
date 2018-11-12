package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 竞拍活动参与状态
 * Created by hh on 2017.10.18 0018.
 */
@Getter
@AllArgsConstructor
public enum AuctionJoinState {
    FINISH(0,"finish","已结束"),
    VIABLE(1,"viable","可以参与"),
    DEPOSIT(2,"deposit","交押金"),
    WAIT_PAY(3,"waitPay","等待支付"),
    SUCCESS(4,"success","竞拍成功"),
    ONESELF(5,"oneself","自己的竞拍"),
    EVALUATE(6,"evaluate","已经评价"),
    ;

    private Integer code;
    private String value;
    private String TypeName;

}
