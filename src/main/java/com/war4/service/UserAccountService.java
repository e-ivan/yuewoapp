package com.war4.service;

import com.war4.base.CutPage;
import com.war4.enums.AccountStatementType;
import com.war4.pojo.UserAccount;
import com.war4.pojo.UserAccountStatement;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/12/13.
 */
public interface UserAccountService {
    void addUserAccount(String userId);
    UserAccount getUserAccountByUserId(String userId);
    void updateUserAccount(String userId, BigDecimal amount, AccountStatementType type,String content);
    void updateUserAccount(String userId, BigDecimal amount, AccountStatementType type);
    CutPage<UserAccountStatement> queryUserAccountStatement(String userId, Integer amountType, Integer dealType, Integer page, Integer limit);
    void goldTransformationBalance(String userId,BigDecimal gold);
}
