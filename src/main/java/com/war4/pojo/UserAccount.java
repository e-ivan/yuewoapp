package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 用户账户表
 * Created by Administrator on 2016/12/13.
 */

@Entity
@Table(name = "user_account")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserAccount {
    @Id
    private String fkUserId;

    private BigDecimal gold;        //金币

    private BigDecimal freezedGold; //冻结金额

    private BigDecimal balance;     //金额

    private BigDecimal freezedBalance;//冻结金额

    private BigDecimal refuseCash;  //不可提现金额

    @Version
    @JsonIgnore
    private Integer version;

    @JsonIgnore
    private Timestamp createTime;                          //创建时间

    @JsonIgnore
    private Integer delFlag;                                //删除标记

}

