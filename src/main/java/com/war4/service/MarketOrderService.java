package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.MarketOrder;
import com.war4.pojo.MarketOrderAddress;
import com.war4.pojo.MarketOrderExpress;
import com.war4.pojo.MarketOrderGoods;

import java.util.Date;

/**
 * 商城订单服务
 * Created by hh on 2017.9.22 0022.
 */
public interface MarketOrderService {
    /**
     * 根据订单编号获取订单
     * @param orderNo
     */
    MarketOrder getMarketOrderByNo(String orderNo);

    /**
     * 根据订单编号获取订单
     * @param orderNo
     * @param address   是否获取地址数据
     */
    MarketOrder getMarketOrderByNo(String orderNo,boolean address);

    /**
     * 设置订单为支付成功
     * @param orderNo
     */
    void setOrderPaySuccess(String orderNo);

    /**
     * 获取订单地址信息
     * @param orderId
     */
    MarketOrderAddress getOrderAddress(Integer orderId);

    /**
     * 获取所有订单
     * @param
     */
    CutPage<MarketOrder> queryAllOrderList(String payway,String paystatus,String keyword,Integer page, Integer limit) throws Exception;


    /**
     * 获取订单所有商品
     * @param
     */
    CutPage<MarketOrderGoods> queryMarketOrderDetail(int orderId ,Integer page, Integer limit);

    /**
     * 添加发货信息
     * @param
     * @param sendTime
     */
    void createMarketOrderExpress(Integer orderId, String express, String trackNo, Date sendTime);

    /**
     * 修改发货信息
     * @param
     * @param sendTime
     */
    void updateMarketOrderExpress(Integer orderId, String express, String trackNo, Date sendTime);

    MarketOrderExpress queryMarketOrderExpress(Integer orderId);

}
