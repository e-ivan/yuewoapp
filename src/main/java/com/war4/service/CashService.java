package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.Cash;

/**
 * Created by Administrator on 2016/12/15.
 */
public interface CashService {
    void addCash(Cash cash);
    void updateCashStatus(Cash cash, boolean agree);
    CutPage<Cash> queryCashForUser(String fkUserId,Integer page,Integer limit);
    CutPage<Cash> queryCashForAll(Integer state,Integer accountType,String keyword,Integer page,Integer limit);
}
