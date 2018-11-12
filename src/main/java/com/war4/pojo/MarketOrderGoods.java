package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 商城订单表
 * Created by hh on 2017.9.21 0021.
 */
@Entity
@Table(name = "market_order_goods")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MarketOrderGoods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer numIid;
    private String skuId;
    private Integer nums;
    private double price;
    private double amount;
    private Integer orderId;
    private String infoJson;
    private  String img;
    private  String skuPropertiesName;

}
