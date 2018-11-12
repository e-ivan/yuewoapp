package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.SystemParameters;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.MarketOrderStatus;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.*;
import com.war4.service.MarketCouponService;
import com.war4.service.MarketOrderService;
import com.war4.vo.PushMsgVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.9.22 0022.
 */
@Service
public class MarketOrderServiceImpl extends BaseService implements MarketOrderService{

    @Autowired
    private MarketCouponService marketCouponService;

    @Override
    public MarketOrder getMarketOrderByNo(String orderNo) {
        return this.getMarketOrderByNo(orderNo,false);
    }

    @Override
    public MarketOrder getMarketOrderByNo(String orderNo, boolean address) {
        String hql = "from MarketOrder where orderNo = :orderNo";
        Map<String,Object> map = new HashMap<>();
        map.put("orderNo",orderNo);
        MarketOrder marketOrder = baseRepository.executeHql(hql, map);
        if (address){
            marketOrder.setOrderAddress(this.getOrderAddress(marketOrder.getId()));
        }
        return marketOrder;
    }

    @Override
    public void setOrderPaySuccess(String orderNo) {
        MarketOrder marketOrder = this.getMarketOrderByNo(orderNo,true);
        if (marketOrder != null && MarketOrderStatus.WAIT.getCode().equals(marketOrder.getStatus())) {
            marketOrder.setStatus(MarketOrderStatus.PAY.getCode());
            marketOrder.setPayTime(new Date());
            baseRepository.updateObj(marketOrder);
            //用户商城订单+1
            //推送信息
            String msg = SystemParameters.MESSAGE_MARKET_ORDER_PAY_SUCCESS + "。订单号:" + marketOrder.getOrderNo();
            PushMsgVO pmv = new PushMsgVO(marketOrder.getUserId(),
                    null, SystemParameters.MESSAGE_MARKET_ORDER_PAY_SUCCESS,
                    msg,marketOrder.getId(),
                    MessageLogType.MARKER_ORDER,
                    true);
            ac.publishEvent(new PushEvent(pmv));
            //smsService.sendAndSaveSMS(msg,marketOrder.getOrderAddress().getPhone(),MessageLogType.MARKER_ORDER,marketOrder.getUserId(),marketOrder.getId().toString());
        }
    }

    @Override
    public MarketOrderAddress getOrderAddress(Integer orderId) {
        String hql = "from MarketOrderAddress where orderId = :orderId";
        Map<String,Object> map = new HashMap<>();
        map.put("orderId",orderId);
        return baseRepository.executeHql(hql,map);
    }

    @Override
    public CutPage<MarketOrder> queryAllOrderList(String payway,String paystatus,String keyword,Integer page, Integer limit) throws Exception {

        StringBuilder sql = new StringBuilder(200);
        Map<String,Object> map = new HashMap<>();
        sql.append("SELECT o.* FROM market_order o LEFT JOIN market_order_address a ON o.id = a.order_id WHERE 1=1 ");
//        hql.append("from MarketOrder o left join MarketOrderAddress a on o.id = a.orderId where 1=1 ");

        if (StringUtils.isNotBlank(keyword)) {
            sql.append(" AND (o.order_no LIKE :keyword OR o.user_id LIKE :keyword OR a.name LIKE :keyword OR a.phone LIKE :keyword ) ");
            map.put("keyword", "%" + keyword + "%");
        }

        if (StringUtils.isNotBlank(payway)) {
            sql.append(" AND (o.pay_status = :payStatus) ");
            map.put("payStatus", payway);
        }

        if (StringUtils.isNotBlank(paystatus)) {
            if(paystatus.equals("15")){  //筛选出 已发货和已付款
                sql.append(" AND (o.status = :status1 ");
                map.put("status1", 1);

                sql.append("OR o.status = :status2) ");
                map.put("status2", 5);
            }
            else {
                sql.append(" AND (o.status = :status) ");
                map.put("status", Integer.parseInt(paystatus));
            }

        }

        sql.append(" ORDER BY o.created_at DESC");

        CutPage<MarketOrder> marketOrderCutPage = baseRepository.executeSql(sql.toString(),map,MarketOrder.class, page,limit);

        for (MarketOrder marketOrder:marketOrderCutPage.getiData()){
            String couponId = marketOrder.getCouponId();
            if (couponId != null && couponId != "0" && couponId.length()>3){
                MarketCoupon marketCoupon = marketCouponService.getMarketCoupon(couponId);
                if (marketCoupon != null){
                    String name = marketCoupon.getName();
                    marketOrder.setCouponMoney(name);
                }
            }
            else {
                marketOrder.setCouponMoney("0");
            }

            MarketOrder marOrder = getMarketOrderByNo(marketOrder.getOrderNo(),true);
            if(marOrder != null){
                marketOrder.setOrderAddress(marOrder.getOrderAddress());
            }
        }
        return marketOrderCutPage;
    }

    @Override
    public  CutPage<MarketOrderGoods> queryMarketOrderDetail(int orderId,Integer page, Integer limit){
        Map<String,Object> map = new HashMap<>();
        String hql = "from MarketOrderGoods where orderId = :orderId ";
        map.put("orderId",orderId);
        return baseRepository.executeHql(hql,map, page,limit);
    }

    @Override
    @Transactional
    public void createMarketOrderExpress(Integer orderId,String express, String trackNo, Date sendTime){
        if (StringUtils.isBlank(express) || StringUtils.isBlank(trackNo)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"快递信息不能为空！");
        }
        MarketOrder marketOrder = baseRepository.getObjById(MarketOrder.class,orderId);
        if(marketOrder == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"此订单不存在!");
        }
        Map<String,Object> map = new HashMap<>();
        String hql = "from MarketOrderExpress where orderId = :orderId ";
        map.put("orderId",orderId);
        MarketOrderExpress me = baseRepository.executeHql(hql,map);
        if (me != null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"此发货信息已存在!");
        }
        MarketOrderExpress marketOrderExpress = new MarketOrderExpress();
        marketOrderExpress.setOrderId(orderId);
        marketOrderExpress.setUserId(marketOrder.getUserId());
        marketOrderExpress.setExpress(express.trim());
        marketOrderExpress.setTrackNo(trackNo.trim());
        marketOrderExpress.setSendTime(sendTime == null ? new Date() : sendTime);
        baseRepository.saveObj(marketOrderExpress);
        //订单设置为发货状态
        marketOrder.setStatus(MarketOrderStatus.DELIVERED.getCode());
        baseRepository.updateObj(marketOrder);
    }

    @Override
    @Transactional
    public void updateMarketOrderExpress(Integer orderId,String express, String trackNo, Date sendTime){
        if (StringUtils.isBlank(express) || StringUtils.isBlank(trackNo)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"快递信息不能为空！");
        }
        MarketOrder marketOrder = baseRepository.getObjById(MarketOrder.class,orderId);
        if(marketOrder == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"此订单不存在!");
        }
        Map<String,Object> map = new HashMap<>();
        String hql = "from MarketOrderExpress where orderId = :orderId ";
        map.put("orderId",orderId);
        MarketOrderExpress me = baseRepository.executeHql(hql,map);
        if (me == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"此发货信息不存在!");
        }
        me.setExpress(express.trim());
        me.setTrackNo(trackNo.trim());
        me.setSendTime(sendTime == null ? new Date() : sendTime);
        baseRepository.updateObj(me);
    }


    @Override
    public MarketOrderExpress queryMarketOrderExpress(Integer orderId){
        Map<String,Object> map = new HashMap<>();
        String hql = "from MarketOrderExpress where orderId = :orderId ";
        map.put("orderId",orderId);
        MarketOrderExpress marketOrderExpress =  baseRepository.executeHql(hql,map);
        if(marketOrderExpress == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"此发货信息不存在!");
        }
        return marketOrderExpress;
    }

}
