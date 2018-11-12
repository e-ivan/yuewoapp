package com.war4.enums;

import com.war4.pojo.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2017/1/19.
 */
@Getter
@AllArgsConstructor
public enum OrderType {
    RECHARGE("recharge","充值订单",RechargeOrder.class),
    MARKET_ORDER("marketOrder","商城订单",MarketOrder.class),
    FILM_ORDER("filmOrder","影票订单",FilmOrder.class),
    BID_ORDER("bidOrder","投标订单",ActivityBidItem.class),
    AUCTION_DEPOSIT_ORDER("auctionDepositOrder","竞拍押金订单",AuctionDeposit.class),
    AUCTION_ORDER("auctionOrder","竞拍订单",AuctionOrder.class),
    AUCTION_ITEM_ORDER("auctionItemOrder","竞拍明细订单",AuctionItemOrder.class),
    FILM_ROOM("filmRoom","开房间的订单",FilmRoom.class),
    VIDEO_CHAT_ORDER("videoChatOrder","视频聊天订单",VideoChatItem.class),
    ;

    private String code;
    private String note;
    private Class belongToClass;

    public static OrderType getByCode(String code){
        for(OrderType type: OrderType.values()){
            if (type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}
