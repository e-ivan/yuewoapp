package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.Recharge;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/12/14.
 */
public interface RechargeService {
    Recharge addRecharge(String fkUserId, BigDecimal money);
    CutPage<Recharge> queryRechargeListByUserId(String fkUserId,Integer page,Integer limit);
    void updateSetRechargeSuccess(String rechargeId);
    Recharge queryRecharge(String orderId);
}
