package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.enums.*;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2016/11/10.
 */
@Service
public class BaseOrderServiceImpl implements BaseOrderService{

    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private FilmOrderService filmOrderService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private FilmRoomService filmRoomService;
    @Autowired
    private MarketOrderService marketOrderService;
    @Autowired
    private ActivityBidService activityBidService;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private RechargeOrderService rechargeOrderService;


    @Override
    public void addBaseOrder(String orderId, OrderType type,String creator,String receiver) {
        BaseOrder baseOrder = new BaseOrder();
        baseOrder.setId(orderId);
        baseOrder.setType(type.getCode());
        baseOrder.setCreator(creator);
        baseOrder.setReceiver(receiver);
        baseRepository.saveObj(baseOrder);
    }

    @Override
    public void addBaseOrder(String orderId, OrderType type, String creator) {
        this.addBaseOrder(orderId, type, creator,null);
    }

    @Override
    public BaseOrder queryBaseOrder(String orderId) {
        return baseRepository.getObjById(BaseOrder.class,orderId);
    }

    @Override
    public boolean checkOrderPayStatus(String orderId, HttpServletResponse response, String responseStr) throws Exception {
        BaseOrder baseOrder = queryBaseOrder(orderId);
        if (baseOrder == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"订单不存在！");
        }
        if(baseOrder.getType().equals(OrderType.FILM_ORDER.getCode())){
            FilmOrder filmOrder = filmOrderService.queryFilmOrderById(orderId);
            if (!filmOrder.getStatus().equals(FilmOrderStatus.CREATE.getCode())) {//已经支付过，重复通知
                if (response != null && responseStr != null) {
                    response.getWriter().print(responseStr);
                }
                return true;
            }
        }
        if(baseOrder.getType().equals(OrderType.RECHARGE.getCode())){
            RechargeOrder rechargeOrder = rechargeOrderService.getRechargeOrder(orderId);
            if (!rechargeOrder.getStatus().equals(RechargeOrderStatus.CREATE.getCode())) {//已经支付过，重复通知
                if (response != null && responseStr != null) {
                    response.getWriter().print(responseStr);
                }
                return true;
            }
        }
        if(baseOrder.getType().equals(OrderType.FILM_ROOM.getCode())){
            FilmRoom filmRoom = filmRoomService.queryFilmRoom(orderId);
            if (!filmRoom.getStatus().equals(FilmRoomStatus.CREATE.getCode())) {//已经支付过，重复通知
                if (response != null && responseStr != null) {
                    response.getWriter().print(responseStr);
                }
                return true;
            }
        }
        if(baseOrder.getType().equals(OrderType.MARKET_ORDER.getCode())){
            MarketOrder marketOrder = marketOrderService.getMarketOrderByNo(orderId);
            if (!marketOrder.getStatus().equals(MarketOrderStatus.WAIT.getCode())) {//已经支付过，重复通知
                if (response != null && responseStr != null) {
                    response.getWriter().print(responseStr);
                }
                return true;
            }
        }
        if(baseOrder.getType().equals(OrderType.BID_ORDER.getCode())){
            ActivityBidItem bidOrder = activityBidService.getActivityBidItemByOrderId(orderId);
            if (!bidOrder.getState().equals(ActivityBidItem.CREATE)) {//已经支付过，重复通知
                if (response != null && responseStr != null) {
                    response.getWriter().print(responseStr);
                }
                return true;
            }
        }
        if(baseOrder.getType().equals(OrderType.AUCTION_DEPOSIT_ORDER.getCode())){
            AuctionDeposit deposit = auctionService.getAuctionDepositByOrderId(orderId);
            if (!deposit.getStatus().equals(AuctionDepositStatus.CREATE.getCode())) {//已经支付过，重复通知
                if (response != null && responseStr != null) {
                    response.getWriter().print(responseStr);
                }
                return true;
            }
        }
        if(baseOrder.getType().equals(OrderType.AUCTION_ITEM_ORDER.getCode())){
            AuctionItemOrder auctionItemOrder = auctionService.getAuctionItemOrderByOrderId(orderId);
            if (!auctionItemOrder.getStatus().equals(AuctionItemOrderStatus.CREATE.getCode())) {//已经支付过，重复通知
                if (response != null && responseStr != null) {
                    response.getWriter().print(responseStr);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkOrderPayStatus(String orderId) throws Exception {
        return this.checkOrderPayStatus(orderId,null,null);
    }
}
