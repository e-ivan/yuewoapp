package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserCashAccount;
import com.war4.pojo.UserInfoBase;
import com.war4.service.UserCashAccountService;
import com.war4.util.BitStateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.11.6 0006.
 */
@Service
public class UserCashAccountServiceImpl extends BaseService implements UserCashAccountService {
    @Override
    @Transactional
    public void updateUserCashAccount(UserCashAccount userCashAccount) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userCashAccount.getUserId());
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"用户不存在");
        }
        UserCashAccount cashAccount = this.getCashAccountByUser(userCashAccount.getUserId());
        if (cashAccount != null){
            cashAccount.setRemark(userCashAccount.getRemark());
            cashAccount.setPhone(userCashAccount.getPhone());
            cashAccount.setAlipayAccount(userCashAccount.getAlipayAccount());
            cashAccount.setAccountName(userCashAccount.getAccountName());
            baseRepository.updateObj(cashAccount);
        }else {
            cashAccount = new UserCashAccount();
            cashAccount.setRemark(userCashAccount.getRemark());
            cashAccount.setPhone(userCashAccount.getPhone());
            cashAccount.setAlipayAccount(userCashAccount.getAlipayAccount());
            cashAccount.setAccountName(userCashAccount.getAccountName());
            cashAccount.setUserId(userCashAccount.getUserId());
            baseRepository.saveObj(cashAccount);
            user.addState(BitStateUtil.OP_BIND_CASH_ACCOUNT);
            baseRepository.updateObj(user);
        }
    }

    @Override
    public UserCashAccount getCashAccountByUser(String userId) {
        String hql = "from UserCashAccount where userId = :userId";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        return baseRepository.executeHql(hql,map);
    }
}
