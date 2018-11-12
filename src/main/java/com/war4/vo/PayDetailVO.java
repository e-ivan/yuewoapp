package com.war4.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单详情临时对象
 * Created by hh on 2017.9.24 0024.
 */
@Getter
@Setter@NoArgsConstructor@AllArgsConstructor
public class PayDetailVO {
    private String title;       //商品信息
    private String detail;      //商品详情
    private String outTradeNo;  //商户订单号
    private BigDecimal totalFee;//支付价格
}
