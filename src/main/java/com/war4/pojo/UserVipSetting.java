package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 会员设置表
 * Created by Administrator on 2017/1/6.
 */
@Entity
@Table(name = "user_vip_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserVipSetting {

    @Id
    private String id;

    @Column(name = "vip_name")
    private String vipName;

    @Column(name = "fk_coupon_id")
    private String fkCouponId;

    @Column(name = "integral")
    private BigDecimal integral;

    @Column(name = "intro")
    private String  intro;

    @Transient
    private Coupon coupon;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
