package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 商城订单地址
 * Created by hh on 2017.9.24 0024.
 */
@Entity
@Table(name = "market_order_express")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MarketOrderExpress {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer orderId;
    private String userId;
    private String express;
    private String trackNo;
    private Integer status;
    private Timestamp created;
    private Date sendTime;
    private Date signTime;
}
