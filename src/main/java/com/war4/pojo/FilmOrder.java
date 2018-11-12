package com.war4.pojo;

import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 电影票订单
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "film_order")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class FilmOrder {

    @Id
    private String fkOrderId;

    @Column(name = "fk_user_id")
    private String fkUserId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "fk_user_coupon_id")
    private String fkUserCouponId;

    @Column(name = "money")
    private BigDecimal money = BigDecimal.ZERO;       //订单总金额（包含服务费、手续费）

    @Column(name = "pay_monry")
    private BigDecimal payMoney = BigDecimal.ZERO;    //实际支付金额

    @Column(name = "unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;   //单价(平均价)

    @Column(name = "reduce_price")
    private BigDecimal reducePrice = BigDecimal.ZERO;   //优惠金额（优惠券）

    @Column(name = "count")
    private Integer count = 1;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "pay_type")
    private String payType;                           //支付方式

    @Column(name = "pay_time")
    private Date payTime;                           //支付时间

    @Column(name = "out_trade_no")
    private String outTradeNo;                          //支付商户订单号

    @Column(name = "handle_fee")
    private BigDecimal handleFee = BigDecimal.ZERO;     //订单总手续费

    @Column(name = "service_fee")
    private BigDecimal serviceFee = BigDecimal.ZERO;    //订单总服务费

    @Column(name = "other_fee")
    private BigDecimal otherFee = BigDecimal.ZERO;      //订单总其它费用

    @Transient
    private JSONArray jsonArray;
    @Transient
    private UserCoupon userCoupon;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
