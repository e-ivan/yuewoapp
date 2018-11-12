package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.Gift;

/**
 * Created by Administrator on 2016/12/13.
 */
public interface GiftService {
    Gift addGift(Gift gift);
    CutPage<Gift> queryGift(Integer page,Integer limit);
    Gift queryGiftById(String giftId);
    void deleteGift(String giftId);
}
