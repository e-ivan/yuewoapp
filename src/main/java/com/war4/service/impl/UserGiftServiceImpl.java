package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.AccountStatementType;
import com.war4.enums.AccountStatus;
import com.war4.enums.AccountType;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.Gift;
import com.war4.pojo.UserGift;
import com.war4.pojo.UserInfoBase;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/12/13.
 */
@Service
public class UserGiftServiceImpl implements UserGiftService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private UserAccountDetailService userAccountDetailService;

    @Override
    @Transactional
    public UserGift addUserGift(String fkUserId, String fkAnchorUserId, String giftId, Integer count,String orderId) {
        UserInfoBase anchorUser = baseRepository.getObjById(UserInfoBase.class, fkAnchorUserId);
        if (anchorUser == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "赠送用户不存在！");
        }

        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, fkUserId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在！");
        }
        if (giftId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "礼物不能为空！");
        }
        if (count == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "礼物数量不能为空！");
        }
        if (fkUserId.equals(fkAnchorUserId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能给自己送礼物哟！");
        }

        Gift gift = giftService.queryGiftById(giftId);
        BigDecimal totalPrice = gift.getGold().multiply(new BigDecimal(count));
        BigDecimal totalPlatformPrice = gift.getExchangePrice().multiply(new BigDecimal(count));

        userAccountService.updateUserAccount(fkUserId, totalPrice, AccountStatementType.GIVE_GIFT,anchorUser.getNickName());
        if (totalPlatformPrice.compareTo(BigDecimal.ZERO) > 0) {
            userAccountService.updateUserAccount(fkAnchorUserId, totalPlatformPrice, AccountStatementType.GAIN_GIFT, user.getNickName());
        }
        UserGift userGift = new UserGift();
        userGift.setId(UUID.randomUUID().toString());
        userGift.setFkUserId(fkUserId);
        userGift.setFkGiftId(giftId);
        userGift.setFkAnchorUserId(fkAnchorUserId);
        userGift.setCount(count);
        userGift.setPrice(totalPrice);
        userGift.setExchangePrice(totalPlatformPrice);
        userGift.setUse(true);
        userGift.setOrderId(orderId);
        baseRepository.saveObj(userGift);
        return userGift;
    }

    @Override
    @Transactional
    public UserGift addUserGift(String fkUserId, String fkAnchorUserId, String giftId, Integer count) {
        UserInfoBase anchorUser = baseRepository.getObjById(UserInfoBase.class, fkAnchorUserId);
        if (anchorUser == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "赠送用户不存在！");
        }

        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, fkUserId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在！");
        }
        if (giftId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "礼物不能为空！");
        }
        if (count == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "礼物数量不能为空！");
        }
        if (fkUserId.equals(fkAnchorUserId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能给自己送礼物哟！");
        }

        Gift gift = giftService.queryGiftById(giftId);
        BigDecimal totalPrice = gift.getGold().multiply(new BigDecimal(count));
        BigDecimal totalPlatformPrice = gift.getExchangePrice().multiply(new BigDecimal(count));

        userAccountService.updateUserAccount(fkUserId, totalPrice, AccountStatementType.GIVE_GIFT,anchorUser.getNickName());
        userAccountService.updateUserAccount(fkAnchorUserId, totalPlatformPrice, AccountStatementType.GAIN_GIFT,user.getNickName());
        UserGift userGift = new UserGift();
        userGift.setId(UUID.randomUUID().toString());
        userGift.setFkUserId(fkUserId);
        userGift.setFkGiftId(giftId);
        userGift.setFkAnchorUserId(fkAnchorUserId);
        userGift.setCount(count);
        userGift.setPrice(totalPrice);
        userGift.setExchangePrice(totalPlatformPrice);
        userGift.setUse(true);
        baseRepository.saveObj(userGift);
        return userGift;
    }

    @Override
    public CutPage<UserGift> queryMyGetGift(String userId, Integer page, Integer limit) {
        if (userId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "userId不能为空！");
        }
        String hql = "from UserGift where delFlag = 0 and fkAnchorUserId = :userId order by createTime desc ";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        CutPage<UserGift> cutPage = baseRepository.executeHql(hql, map, page, limit);
        for (UserGift userGift : cutPage.getiData()) {
            assembleService.assembleObject(userGift);
        }
        return cutPage;
    }

    @Override
    public CutPage<UserGift> queryMySendGift(String userId, Integer page, Integer limit) {
        if (userId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "userId不能为空！");
        }
        String hql = "from UserGift where delFlag = 0 and fkUserId = :userId order by createTime desc ";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        CutPage<UserGift> cutPage = baseRepository.executeHql(hql, map, page, limit);
        for (UserGift userGift : cutPage.getiData()) {
            assembleService.assembleObject(userGift);
        }
        return cutPage;
    }

    @Override
    @Transactional
    public void returnUserGift(String userGiftId) {
        UserGift userGift = baseRepository.getObjById(UserGift.class, userGiftId);
        if (userGift == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该礼物！");
        }
        Gift gift = giftService.queryGiftById(userGift.getFkGiftId());
        BigDecimal total = gift.getGold().multiply(new BigDecimal(userGift.getCount()));
        userAccountDetailService.addUserAccountDetail(userGift.getFkUserId(), AccountType.GOLD.getCode(), AccountStatus.INCOME.getCode(), total);
        userAccountDetailService.addUserAccountDetail(userGift.getFkAnchorUserId(), AccountType.GOLD.getCode(), AccountStatus.COST.getCode(), total);

    }

    @Override
    public UserGift queryUserGiftById(String userGiftId) {
        UserGift userGift = baseRepository.getObjById(UserGift.class, userGiftId);
        if (userGift == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该礼物！");
        }
        userGift = assembleService.assembleObject(userGift);
        return userGift;
    }
}
