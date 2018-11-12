package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 充值订单
 * Created by hh on 2017.11.7 0007.
 */
@Entity
@Table(name = "recharge_order")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class RechargeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private BigDecimal price;

    private String orderId;//订单编号

    private String outTradeNo;//商户订单号

    private BigDecimal payMoney;//实际支付金额

    private Date payTime;//支付时间

    private String payType;//支付类型

    private Integer status;//支付状态

    @Version//定义为version
    @JsonIgnore//不对外输出
    private Integer version;//版本，乐观锁用

    private Timestamp created;
}
