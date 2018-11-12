package com.war4.service;

import com.war4.enums.OrderType;
import com.war4.pojo.BaseOrder;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2016/11/10.
 */
public interface BaseOrderService {
    void addBaseOrder(String orderId,OrderType type,String creator,String receiver);
    void addBaseOrder(String orderId,OrderType type,String creator);

    BaseOrder queryBaseOrder(String orderId);

    /**
     * 支付宝检查支付状态
     * @param responseStr 响应内容
     * @return 支付返回true，未支付返回false
     */
    boolean checkOrderPayStatus(String orderId, HttpServletResponse response,String responseStr) throws Exception;

    /**
     * 微信检查支付状态
     * @return 支付返回true，未支付返回false
     */
    boolean checkOrderPayStatus(String orderId) throws Exception;

}
