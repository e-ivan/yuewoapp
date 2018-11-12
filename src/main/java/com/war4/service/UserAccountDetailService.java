package com.war4.service;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/12/13.
 */
public interface UserAccountDetailService {
    void addUserAccountDetail(String fkUserId, String accountType, Integer status, BigDecimal total);

    BigDecimal queryUserAccountForBalance(String userId);

    BigDecimal queryUserAccountForGold(String userId);

    void addgold(String userId,BigDecimal glod);
}
