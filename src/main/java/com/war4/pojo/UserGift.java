package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 我的礼物
 * Created by Administrator on 2016/12/13.
 */

@Entity
@Table(name = "user_gift")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserGift {

    @Id
    private String id;

    @Column(name = "fk_user_id")
    private String fkUserId;

    @Column(name = "fk_anchor_user_id")
    private String fkAnchorUserId;
    @Column(name = "fk_gift_id")
    private String fkGiftId;
    @Column(name = "count")
    private Integer count;
    @Column(name = "is_use")
    private Boolean isUse;

    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "exchange_price")
    private BigDecimal exchangePrice;

    @Transient
    private Gift gift;

    @Transient
    private UserInfoBase anchor;

    @Transient
    private UserInfoBase giver;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

    @Column(name = "order_id")
    private String orderId;                                //订单id
    public Boolean getUse() {
        return isUse;
    }

    public void setUse(Boolean use) {
        isUse = use;
    }

}
