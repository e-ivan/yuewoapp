package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.AccountStatementType;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.OrderType;
import com.war4.enums.RechargeOrderStatus;
import com.war4.pojo.RechargeOrder;
import com.war4.pojo.UserInfoBase;
import com.war4.service.BaseOrderService;
import com.war4.service.RechargeOrderService;
import com.war4.service.UserAccountService;
import com.war4.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.11.7 0007.
 */
@Service
public class RechargeOrderServiceImpl extends BaseService implements RechargeOrderService {

    @Autowired
    private BaseOrderService baseOrderService;

    @Autowired
    private UserAccountService userAccountService;
    @Override
    @Transactional
    public RechargeOrder createRechargeOrder(String userId, BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"充值金额必须大于0");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"用户不存在");
        }
        RechargeOrder rechargeOrder = new RechargeOrder();
        String orderId = OrderUtil.createOrderId(OrderUtil.RECHARGE);
        rechargeOrder.setOrderId(orderId);
        rechargeOrder.setUserId(userId);
        rechargeOrder.setPrice(amount);
        rechargeOrder.setPayMoney(rechargeOrder.getPrice());
        rechargeOrder.setStatus(RechargeOrderStatus.CREATE.getCode());
        baseRepository.saveObj(rechargeOrder);
        baseOrderService.addBaseOrder(orderId, OrderType.RECHARGE,userId);
        return rechargeOrder;
    }

    @Override
    @Transactional
    public void cancelRechargeOrder(String orderId) {
        RechargeOrder rechargeOrder = this.getRechargeOrder(orderId);
        if (rechargeOrder != null) {
            if (RechargeOrderStatus.PAY.getCode().equals(rechargeOrder.getStatus())) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单取消失败");
            }
            if (RechargeOrderStatus.CREATE.getCode().equals(rechargeOrder.getStatus())) {
                rechargeOrder.setStatus(RechargeOrderStatus.CANCEL.getCode());
                baseRepository.updateObj(rechargeOrder);
            }
        }
    }

    @Override
    public RechargeOrder getRechargeOrder(String orderId) {
        String hql = "from RechargeOrder where orderId = :orderId";
        Map<String,Object> map = new HashMap<>();
        map.put("orderId",orderId);
        return baseRepository.executeHql(hql,map);
    }

    @Override
    public CutPage<RechargeOrder> queryUserRechargeOrder(String userId,Integer page,Integer limit) {
        String hql = "from RechargeOrder where userId = :userId and status = :status order by created desc";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("status",RechargeOrderStatus.PAY.getCode());
        return baseRepository.executeHql(hql,map,page,limit);
    }

    @Override
    @Transactional
    public void cancelOverdueRechargeOrder(Integer hour) {
        String hql = "from RechargeOrder where status = :status and timesTampDiff(HOUR,created,NOW()) >= :hour";
        Map<String,Object> map = new HashMap<>();
        map.put("status",RechargeOrderStatus.CREATE.getCode());
        map.put("hour",hour);
        CutPage<RechargeOrder> cutPage = baseRepository.executeHql(hql, map, 0, CutPage.MAX_COUNT);
        for (RechargeOrder order : cutPage.getiData()) {
            order.setStatus(RechargeOrderStatus.CANCEL.getCode());
            baseRepository.updateObj(order);
        }
    }

    @Override
    @Transactional
    public void setOrderPaySuccess(String orderId) {
        RechargeOrder order = this.getRechargeOrder(orderId);
        if (order != null && RechargeOrderStatus.CREATE.getCode().equals(order.getStatus())){
            order.setStatus(RechargeOrderStatus.PAY.getCode());
            order.setPayTime(new Date());
            baseRepository.updateObj(order);
            userAccountService.updateUserAccount(order.getUserId(),order.getPrice(), AccountStatementType.RECHARGE);
        }
    }
}
