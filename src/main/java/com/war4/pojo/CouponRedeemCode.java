package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 优惠券兑换码
 */
@Entity
@Table(name = "coupon_redeem_code")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class CouponRedeemCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String redeemCode;

    private String adminId;

    private String mobile;

    private Long splitType;

    private Timestamp created;

    private boolean isUse;

    private String userId;

    private Date useTime;

    private String intro;

    private int source;

    @Version
    private int version;

    private int type;

    @Transient
    private String splitTypeName;//字典数据明细名称

}