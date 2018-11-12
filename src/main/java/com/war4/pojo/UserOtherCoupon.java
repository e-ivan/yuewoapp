package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 用户优惠券
 * Created by Administrator on 2016/12/13.
 */

@Entity
@Table(name = "user_other_coupon")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserOtherCoupon {

    @Id
    private String id;

    @Column(name = "fk_user_id")
    private String fkUserId;
    @Column(name = "fk_coupon_id")
    private String fkCouponId;


    @Transient
    private Coupon coupon;

    @Column(name = "off_time")
    private Date offTime;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间


    @Column(name = "type")
    private Integer type;                                //删除标记

    @Column(name = "object_id")
    private String objectId;                                //活动id
}
