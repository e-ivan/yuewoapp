package com.war4.service;

import java.util.Map;

/**
 * Created by Administrator on 2017/2/9.
 */
public interface PayUtilService {
    void payService(String orderId) throws Exception;
    /**
     * 创建支付宝订单
     * @param orderId   订单id
     * @return          下单时的orderInfo
     */
    String createAlipayOrder(String orderId) throws Exception;

    /**
     * 创建微信订单
     * @param ip
     * @param orderId
     * @param clientType
     */
    Map<String,String> createWechatOrder(String ip,String orderId,String clientType) throws Exception;

    /**
     * 余额支付订单
     * @param userId
     * @param orderId
     */
    void balancePayOrder(String userId,String orderId) throws Exception;


}
