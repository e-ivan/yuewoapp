package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 竞拍出价明细
 * Created by hh on 2017.11.1 0001.
 */
@Entity
@Table(name = "auction_item")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class AuctionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long auctionId;

    private String userId;

    private String nickname;

    private String headImg;

    private BigDecimal bidPrice;

    private BigDecimal prevBidPrice;

    private Long prevId;

    private Integer status;

    private Timestamp created;
}
