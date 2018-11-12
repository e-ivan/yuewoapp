package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 商城订单地址
 * Created by hh on 2017.9.24 0024.
 */
@Entity
@Table(name = "market_order_address")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MarketOrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer orderId;
    private String name;
    private String phone;
    private String address;

}
