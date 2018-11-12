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
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "user_account_detail")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserAccountDetail {
    @Id
    private String id;


    @Column(name = "fk_user_id")
    private String fkUserId;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "status")
    private Integer status;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
