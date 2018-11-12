package com.war4.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 商城兑换券拆分规则
 * Created by hh on 2017.9.18 0018.
 */
@Getter
@Setter
public class MarketCouponExpressionVO {
    private Integer totalCount;  //总数量
    private Integer valid;  //有效天数
    private List<Coupon> item; //拆分明细

    @Getter
    @Setter
    public static class Coupon {
        private Integer count;  //数量
        private Double money;   //金额
        private Double minUse;  //最低使用金额
    }
}
