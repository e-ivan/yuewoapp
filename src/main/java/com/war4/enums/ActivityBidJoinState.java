package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 投标活动参与状态
 * Created by hh on 2017.10.18 0018.
 */
@Getter
@AllArgsConstructor
public enum ActivityBidJoinState {
    FINISH(0,"finish","已结束"),
    VIABLE(1,"viable","可以参与"),
    FULL(2,"full","已满员"),
    MANUAL_JOIN(3,"manualJoin","手动参与"),
    SYSTEM_JOIN(4,"systemJoin","自动参与"),
    VICTOR(5,"victor","已中奖");


    private Integer code;
    private String value;
    private String TypeName;
}
