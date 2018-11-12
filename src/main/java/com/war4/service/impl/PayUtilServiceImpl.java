package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.PropertiesConfig;
import com.war4.enums.*;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.util.AlipayUtil;
import com.war4.util.OrderUtil;
import com.war4.util.WechatPayUtil;
import com.war4.vo.PayDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/9.
 */
@Service
public class PayUtilServiceImpl implements PayUtilService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private FilmRoomService filmRoomService;

    @Autowired
    private FilmOrderService filmOrderService;

    @Autowired
    private MarketOrderService marketOrderService;

    @Autowired
    private ActivityBidService activityBidService;

    @Autowired
    private AuctionService auctionService;
    @Autowired
    private UserAccountService accountService;
    @Autowired
    private RechargeOrderService rechargeOrderService;

    @Autowired
    private VideoChatService videoChatService;

    @Override
    @Transactional
    public void payService(String orderId) throws Exception {
        BaseOrder baseOrder = baseRepository.getObjById(BaseOrder.class, orderId);
        if (baseOrder != null) {
            if (baseOrder.getType().equals(OrderType.RECHARGE.getCode())) {
                rechargeOrderService.setOrderPaySuccess(baseOrder.getId());
            }
            if (baseOrder.getType().equals(OrderType.FILM_ROOM.getCode())) {
                FilmRoom filmRoom = baseRepository.getObjById(FilmRoom.class, baseOrder.getId());
                if (filmRoom != null) {
                    filmRoomService.updateFilmRoomStatus(baseOrder.getId());
                }
            }
            if (baseOrder.getType().equals(OrderType.FILM_ORDER.getCode())) {
                filmOrderService.setOrderPaySuccess(baseOrder.getId());
            }
            if (baseOrder.getType().equals(OrderType.MARKET_ORDER.getCode())) {
                marketOrderService.setOrderPaySuccess(baseOrder.getId());
            }
            if (baseOrder.getType().equals(OrderType.BID_ORDER.getCode())) {
                activityBidService.setOrderPaySuccess(baseOrder.getId());
            }
            if (baseOrder.getType().equals(OrderType.AUCTION_DEPOSIT_ORDER.getCode())) {
                auctionService.setDepositOrderPaySuccess(baseOrder.getId());
            }
            if (baseOrder.getType().equals(OrderType.AUCTION_ITEM_ORDER.getCode())) {
                auctionService.setAuctionItemOrderPaySuccess(baseOrder.getId());
            }
            if(baseOrder.getType().equals(OrderType.VIDEO_CHAT_ORDER.getCode())){
                videoChatService.setOrderPaySuccess(baseOrder.getId());
            }
            baseOrder.setStatus(BaseOrderStatus.PAY.getCode());
            baseRepository.updateObj(baseOrder);
        }
    }


    @Override
    @Transactional
    public String createAlipayOrder(String orderId) {
        final String payType = OrderPayType.ALIPAY.getValue();
        PayDetailVO payDetail = this.baseOrderDispatcher(orderId, payType);
        return AlipayUtil.getPayInfo(payDetail.getTitle(), payDetail.getDetail(), PropertiesConfig.isProduction() ? payDetail.getTotalFee() :new BigDecimal("0.01"), payDetail.getOutTradeNo());
    }

    @Override
    @Transactional
    public Map<String, String> createWechatOrder(String ip, String orderId, String clientType) throws Exception {
        final String payType = OrderPayType.WXPAY.getValue();
        PayDetailVO payDetail = this.baseOrderDispatcher(orderId, payType);
        return WechatPayUtil.createPayParams(ip, clientType, PropertiesConfig.isProduction() ? payDetail.getTotalFee() :new BigDecimal("0.01"),payDetail.getOutTradeNo(),payDetail.getTitle());
    }

    @Override
    @Transactional
    public void balancePayOrder(String userId,String orderId) throws Exception {
        final String payType = OrderPayType.BALANCE.getValue();
        PayDetailVO payDetail = this.baseOrderDispatcher(orderId, payType);
        accountService.updateUserAccount(userId,payDetail.getTotalFee(),AccountStatementType.ORDER_PAY,orderId);
        this.payService(orderId);
    }

    /**
     * 通用订单分发器
     * @param orderId
     * @param payType
     */
    private PayDetailVO baseOrderDispatcher(String orderId,String payType){
        BaseOrder baseOrder = baseRepository.getObjById(BaseOrder.class, orderId);
        if (baseOrder == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不存在的订单");
        }
        PayDetailVO payDetail = new PayDetailVO();
        if (baseOrder.getType().equals(OrderType.RECHARGE.getCode())) {
            //如果是使用余额充值，则拒绝
            if (OrderPayType.BALANCE.getValue().equals(payType)){
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能使用余额支付方式充值");
            }
            payDetail = this.rechargeOrderOperation(orderId,payType);
        }
        if (baseOrder.getType().equals(OrderType.FILM_ROOM.getCode())) {
            FilmRoom filmRoom = baseRepository.getObjById(FilmRoom.class, baseOrder.getId());
            if (filmRoom != null) {
            }
        }
        if (baseOrder.getType().equals(OrderType.FILM_ORDER.getCode())) {
            payDetail = this.filmOrderOperation(orderId, payType);
        }
        if (baseOrder.getType().equals(OrderType.MARKET_ORDER.getCode())) {
            payDetail = this.marketOrderOperation(orderId);
        }
        if (baseOrder.getType().equals(OrderType.BID_ORDER.getCode())) {
            payDetail = this.bidOrderOperation(orderId,payType);
        }
        if (baseOrder.getType().equals(OrderType.AUCTION_DEPOSIT_ORDER.getCode())) {
            payDetail = this.auctionDepositOrderOperation(orderId,payType);
        }
        if (baseOrder.getType().equals(OrderType.AUCTION_ITEM_ORDER.getCode())) {
            payDetail = this.auctionOrderOperation(orderId,payType);
        }
        return payDetail;
    }


    /**
     * 充值订单操作
     * @param orderId
     * @param payType
     * @return
     */
    private PayDetailVO rechargeOrderOperation(String orderId, String payType) {
        RechargeOrder rechargeOrder = rechargeOrderService.getRechargeOrder(orderId);
        if (rechargeOrder == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "商品订单不存在！");
        }
        if (!RechargeOrderStatus.CREATE.getCode().equals(rechargeOrder.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单已过期");
        }
        String title = "余额充值";
        String detail = "订单金额";
        String outTradeNo = OrderUtil.addOrderSuffix(rechargeOrder.getOrderId());
        rechargeOrder.setOutTradeNo(outTradeNo);
        rechargeOrder.setPayType(payType);
        baseRepository.updateObj(rechargeOrder);
        return new PayDetailVO(title,detail,outTradeNo,rechargeOrder.getPayMoney());
    }


    /**
     * 电影订单操作
     * @param orderId
     * @param payType
     */
    private PayDetailVO filmOrderOperation(String orderId, String payType) {
        //商品信息title     支付宝：subject     微信：body
        //商品详情detail    支付宝：body        微信：detail
        FilmOrder filmOrder = baseRepository.getObjById(FilmOrder.class, orderId);
        if (filmOrder == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "电影订单不存在！");
        }
        if (!FilmOrderStatus.CREATE.getCode().equals(filmOrder.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单已过期");
        }
        String title = "电影购票";
        String detail = "订单金额";
        String outTradeNo = OrderUtil.addOrderSuffix(filmOrder.getFkOrderId());
        //获取订单金额
        filmOrder.setOutTradeNo(outTradeNo);
        filmOrder.setPayType(payType);
        baseRepository.saveObj(filmOrder);
        return new PayDetailVO(title, detail, outTradeNo, filmOrder.getPayMoney());
    }

    /**
     * 商城订单操作
     * @param orderId
     * marketOrderOperation
     */
    private PayDetailVO marketOrderOperation(String orderId) {
        MarketOrder marketOrder = marketOrderService.getMarketOrderByNo(orderId);
        if (marketOrder == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "商品订单不存在！");
        }
        if (!MarketOrderStatus.WAIT.getCode().equals(marketOrder.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单已过期");
        }
        String title = "商城购物";
        String detail = "订单金额";
        String outTradeNo = OrderUtil.addOrderSuffix(marketOrder.getOrderNo());
        marketOrder.setOutTradeNo(outTradeNo);
        baseRepository.updateObj(marketOrder);
        return new PayDetailVO(title,detail,outTradeNo,marketOrder.getRealAmount());
    }

    /**
     * 投标活动订单操作
     * @param orderId
     * @param payType
     */
    private PayDetailVO bidOrderOperation(String orderId,String payType) {
        ActivityBidItem bidOrder = activityBidService.getActivityBidItemByOrderId(orderId);
        if (bidOrder == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "商品订单不存在！");
        }
        ActivityBid bid = baseRepository.getObjById(ActivityBid.class, bidOrder.getBidId());
        if (ActivityBid.STATE_CREATE.equals(bid.getState()) ||
                ActivityBid.STATE_OVER.equals(bid.getState()) ||
                !ActivityBidItem.CREATE.equals(bidOrder.getState())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单已过期");
        }
        String title = "一元夺票";
        String detail = "订单金额";
        String outTradeNo = OrderUtil.addOrderSuffix(bidOrder.getOrderId());
        bidOrder.setOutTradeNo(outTradeNo);
        bidOrder.setPayType(payType);
        baseRepository.updateObj(bidOrder);
        return new PayDetailVO(title,detail,outTradeNo,bidOrder.getPayMoney());
    }

    /**
     * 竞拍押金订单操作
     * @param orderId
     * @param payType
     */
    private PayDetailVO auctionDepositOrderOperation(String orderId,String payType) {
        AuctionDeposit deposit = auctionService.getAuctionDepositByOrderId(orderId);
        if (deposit == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "商品订单不存在！");
        }
        Auction auction = baseRepository.getObjById(Auction.class, deposit.getAuctionId());
        if (!AuditStatus.PASS.getCode().equals(auction.getStatus()) ||
                !AuctionDepositStatus.CREATE.getCode().equals(deposit.getStatus()) ) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单已过期");
        }
        String title = "约红人竞拍押金";
        String detail = "订单金额";
        String outTradeNo = OrderUtil.addOrderSuffix(deposit.getOrderId());
        deposit.setOutTradeNo(outTradeNo);
        deposit.setPayType(payType);
        baseRepository.updateObj(deposit);
        return new PayDetailVO(title,detail,outTradeNo,deposit.getPayMoney());
    }

    /**
     * 竞拍订单操作
     * @param orderId
     * @param payType
     */
    private PayDetailVO auctionOrderOperation(String orderId, String payType) {
        AuctionItemOrder auctionItemOrder = auctionService.getAuctionItemOrderByOrderId(orderId);
        if (auctionItemOrder == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "商品订单不存在！");
        }
        Auction auction = baseRepository.getObjById(Auction.class, auctionItemOrder.getAuctionId());
        if (!AuditStatus.PASS.getCode().equals(auction.getStatus()) ||
                !AuctionItemOrderStatus.CREATE.getCode().equals(auctionItemOrder.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "订单已过期");
        }
        String title = "约红人竞拍支付";
        String detail = "订单金额";
        String outTradeNo = OrderUtil.addOrderSuffix(auctionItemOrder.getOrderId());
        auctionItemOrder.setOutTradeNo(outTradeNo);
        auctionItemOrder.setPayType(payType);
        baseRepository.updateObj(auctionItemOrder);
        return new PayDetailVO(title,detail,outTradeNo, auctionItemOrder.getPayMoney());
    }
}
