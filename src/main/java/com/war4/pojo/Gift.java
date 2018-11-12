package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "anchor_gift")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Gift {

    @Id
    private String id;

    @Column(name = "name")
    private String name;
    @Column(name = "gold")
    private BigDecimal gold;
    @Column(name = "exchange_price")
    private BigDecimal exchangePrice;       //交易价格

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
