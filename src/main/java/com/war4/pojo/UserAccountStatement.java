package com.war4.pojo;

import com.war4.enums.AccountArithmetic;
import com.war4.enums.AccountStatementType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 账户流水
 * Created by hh on 2017.10.17 0017.
 */
@Entity
@Table(name = "user_account_statement")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserAccountStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountUser;

    private Timestamp created;

    private String accountType;         //账户类型

    private String remark;             //摘要

    private BigDecimal amount;          //操作金额

    private BigDecimal usableAmount;     //可用金额

    private BigDecimal freezedAmount;     //冻结金额

    private Integer dealType;              //交易类型

    private String dealAccount;         //交易账户

    private Integer amountType;         //金额类型  0：可用  1：冻结

    private Boolean inOrOut;

    public String getDealTypeStr(){
        AccountStatementType type = AccountStatementType.getByCode(dealType);
        if (type != null){
            return type.getTypeName();
        }
        return null;
    }

    public String getAccountTypeStr(){
        AccountStatementType type = AccountStatementType.getByCode(dealType);
        if (type != null){
            return type.getAccountType().getValue();
        }
        return null;
    }

    public String getAmountColor(){
        AccountStatementType type = AccountStatementType.getByCode(dealType);
        if (type != null) {
            AccountArithmetic arithmetic = type.getArithmetic();
            if (AccountArithmetic.USABLE_ADD.equals(arithmetic) || AccountArithmetic.USABLE_ADD_FREEZE_MINUS.equals(arithmetic)) {
                return "red";
            }else if (AccountArithmetic.USABLE_MINUS.equals(arithmetic) || AccountArithmetic.USABLE_MINUS_FREEZE_ADD.equals(arithmetic)){
                return "green";
            }
        }
        return "";
    }
    //获取运算符
    public Boolean getSymbol(){
        AccountStatementType type = AccountStatementType.getByCode(dealType);
        if (type != null) {
            AccountArithmetic arithmetic = type.getArithmetic();
            if (AccountArithmetic.USABLE_ADD.equals(arithmetic) ||
                    AccountArithmetic.USABLE_ADD_FREEZE_MINUS.equals(arithmetic)) {
                return true;
            }else if (AccountArithmetic.USABLE_MINUS.equals(arithmetic) ||
                    AccountArithmetic.USABLE_MINUS_FREEZE_ADD.equals(arithmetic)){
                return false;
            }
        }
        return null;
    }
}
