package com.war4.util;

import com.war4.enums.AuctionItemOrderStatus;
import com.war4.pojo.AuctionItemOrder;
import com.war4.service.AuctionService;
import com.war4.service.FilmOrderService;
import com.war4.service.RechargeOrderService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 订单定时器
 * Created by hh on 2017.11.8 0008.
 */
@Component
public class OrderTimeTask {
    @Autowired
    private RechargeOrderService rechargeOrderService;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private FilmOrderService filmOrderService;

    //处理充值过期订单
    //每小时0点刷新
    @Scheduled(cron = "7 0/60 * * * ?")
    public void overdueRechargeOrder() {
        System.err.println("OrderTimeTask.overdueRechargeOrder:"+DateFormatUtils.format(new Date(),"HH:mm:ss"));
        rechargeOrderService.cancelOverdueRechargeOrder(1);
    }

    //处理过期电影订单
    //每小时0点刷新
    @Scheduled(cron = "6 0/30 * * * ?")
    public void disposeOverdueOrder() {
        filmOrderService.disposeOverdueOrder(16);
    }

    //竞拍订单过期未付款
    //每小时整点5秒
//    @Scheduled(cron = "5 0/60 * * * ?")
    public void handleOverdueAuctionOrder() {
        System.err.println("TimeTaskService.handleOverdueAuctionOrder:" + DateFormatUtils.format(new Date(),"HH:mm:ss"));
        List<AuctionItemOrder> auctionItemOrders = auctionService.queryOverdueAuctionOrder(AuctionItemOrderStatus.CREATE.getCode(),24);
        for (AuctionItemOrder auctionItemOrder : auctionItemOrders) {
            auctionService.disposeAuctionOrderCancel(auctionItemOrder);
        }
    }
}
