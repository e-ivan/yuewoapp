package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 用户提现账户
 * Created by hh on 2017.11.6 0006.
 */
@Entity
@Table(name = "user_cash_account")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserCashAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String alipayAccount;

    private String accountName;

    private String phone;

    private String remark;

}
