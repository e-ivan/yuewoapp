package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.RechargeOrder;

import java.math.BigDecimal;

/**
 * 充值订单服务
 * Created by hh on 2017.11.7 0007.
 */
public interface RechargeOrderService {

    //创建充值订单
    RechargeOrder createRechargeOrder(String userId, BigDecimal amount);

    //取消充值订单
    void cancelRechargeOrder(String orderId);

    //获取用户充值订单
    RechargeOrder getRechargeOrder(String orderId);

    //获取用户充值记录
    CutPage<RechargeOrder> queryUserRechargeOrder(String userId,Integer page,Integer limit);

    //取消过时订单
    void cancelOverdueRechargeOrder(Integer hour);

    //充值订单成功
    void setOrderPaySuccess(String orderId);
}
