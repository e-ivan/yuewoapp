package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 商城优惠券
 */
@Entity
@Table(name = "market_coupon")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MarketCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String couponId;

    private String userId;

    private String name;

    private BigDecimal money;

    private boolean isUse;

    private Timestamp created;

    private int valid;

    private Date offTime;

    private BigDecimal minUse;

    private Date useTime;

    private int source;
}