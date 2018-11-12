package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 竞拍订单
 * Created by hh on 2017.9.21 0021.
 */
@Entity
@Table(name = "auction_order")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class AuctionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long auctionId;

    private String userId;      //创建方

    private String bidUserId;   //中拍方

    private String orderId;

    private BigDecimal price;

    private Timestamp created;

    private Integer status;

    @Version
    @JsonIgnore
    private Integer version;
}
