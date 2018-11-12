package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 优惠券
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "coupon")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Coupon {

    public static final Integer NORMAL = 0;     //普通类型：按金额
    public static final Integer COMMON = 1;     //通用类型：按座位

    @Id
    private String id;

    private String name;
    private BigDecimal money;
    private Integer prescription;

    private Timestamp createTime;                          //创建时间

    private Integer delFlag;                                //删除标记

    private Integer type;   //类型  0：普通  1：通用

    private String movieId;

    private Integer seats;

    private String pic;

//    private BigDecimal minValue;
}
