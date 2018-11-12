package com.war4.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 电影兑换券拆分规则
 * Created by hh on 2017.9.18 0018.
 */
@Getter
@Setter
public class FilmCouponExpressionVO {
    private Integer count;  //数量
    private String couponId;  //最低使用金额
}
