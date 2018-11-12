package com.war4.pojo;

import com.war4.base.BaseAuditDomain;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 提现
 * Created by Administrator on 2016/12/14.
 */
@Entity
@Table(name = "cash")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Cash extends BaseAuditDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String note;        //申请留意

    private BigDecimal amount;  //申请金额

    private Integer accountType;//账户类型

    private String account;     //账户

    private String accountName; //账户用户名

    private String bankName;    //银行名称

    @Transient
    private UserCashAccount ca;//用户提现账户

    private Integer delFlag;
}
