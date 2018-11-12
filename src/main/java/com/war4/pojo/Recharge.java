package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 充值
 * Created by Administrator on 2016/12/14.
 */
@Entity
@Table(name = "recharge")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Recharge {

    @Id
    private String id;


    @Column(name = "fk_user_id")
    private String fkUserId;

    @Column(name = "money")
    private BigDecimal money;

    @Column(name = "status")
    private Integer status;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
