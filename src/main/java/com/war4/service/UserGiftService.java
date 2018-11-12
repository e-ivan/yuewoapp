package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.UserGift;

/**
 * Created by Administrator on 2016/12/13.
 */
public interface UserGiftService {
    UserGift addUserGift(String fkUserId, String fkAnchorUserId, String giftId, Integer count,String orderId);
    UserGift addUserGift(String fkUserId, String fkAnchorUserId, String giftId, Integer count);
    CutPage<UserGift> queryMyGetGift(String userId,Integer page,Integer limit);
    CutPage<UserGift> queryMySendGift(String userId,Integer page,Integer limit);
    void returnUserGift(String userGiftId);
    UserGift queryUserGiftById(String userGiftId);
}
