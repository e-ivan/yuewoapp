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
 * 竞拍押金
 * Created by hh on 2017.10.31 0031.
 */
@Entity
@Table(name = "auction_deposit")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class AuctionDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long auctionId;

    private String userId;

    private BigDecimal deposit;

    private Timestamp created;

    private String orderId;

    private String outTradeNo;

    private BigDecimal payMoney;

    private Date payTime;

    private String payType;

    private Integer status;

    @JsonIgnore
    @Version
    private Integer version;

}
