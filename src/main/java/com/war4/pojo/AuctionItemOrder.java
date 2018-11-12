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
 * 竞拍明细订单
 * Created by hh on 2017.9.21 0021.
 */
@Entity
@Table(name = "auction_item_order")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class AuctionItemOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long auctionId;

    private Long auctionItemId;

    private Long depositId;

    private String userId;

    private String orderId;

    private BigDecimal price;

    private Timestamp created;

    private BigDecimal payMoney;

    private String payType;

    private Integer status;

    private String outTradeNo;

    private Date payTime;

    @Version
    @JsonIgnore
    private Integer version;
}
