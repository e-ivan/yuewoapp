package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商城订单表
 * Created by hh on 2017.9.21 0021.
 */
@Entity
@Table(name = "market_order")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MarketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String orderNo;
    private Integer goodsNums;
    private BigDecimal goodsAmount;
    private String couponId;
    private BigDecimal couponAmount;
    private String payStatus;
    private BigDecimal realAmount;
    private Integer status;
    private Integer createdAt;
    private String userId;
    private BigDecimal freight;
    private String outTradeNo;
    private Date payTime;

    @Transient
    private MarketOrderAddress orderAddress;

//    private String name;
//
//
//    private String phone;

    @Transient
    private String couponMoney;
}
