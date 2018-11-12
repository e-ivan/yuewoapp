package com.war4.service;

import com.war4.pojo.UserCashAccount;

/**
 * 用户提现账户服务
 * Created by hh on 2017.11.6 0006.
 */
public interface UserCashAccountService {
    //创建用户提现账户
    void updateUserCashAccount(UserCashAccount userCashAccount);

    //根据用户查找订单账户
    UserCashAccount getCashAccountByUser(String userId);
}
