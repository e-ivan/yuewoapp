package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.AccountArithmetic;
import com.war4.enums.AccountStatementType;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserAccount;
import com.war4.pojo.UserAccountStatement;
import com.war4.repository.BaseRepository;
import com.war4.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public void addUserAccount(String userId) {
        if (userId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户Id不能为空！");
        }
        UserAccount userAccount = new UserAccount();
        userAccount.setFkUserId(userId);
        userAccount.setGold(new BigDecimal(0));
        userAccount.setBalance(new BigDecimal(0));
        baseRepository.saveObj(userAccount);
    }

    @Override
    public UserAccount getUserAccountByUserId(String userId) {
        if (userId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户Id不能为空！");
        }
        UserAccount userAccount = baseRepository.getObjById(UserAccount.class, userId);
        if (userAccount == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "没有找到这个用户！");
        }
        return userAccount;
    }

    @Override
    @Transactional
    public void updateUserAccount(String userId, BigDecimal amount, AccountStatementType type,String content) {
        if (amount.compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"金额不能小于0");
        }else if (amount.compareTo(BigDecimal.ZERO) == 0){
            return;
        }
        UserAccount userAccount = this.operationAccountAmount(userId, amount, type);
        UserAccountStatement uas = new UserAccountStatement();
        baseRepository.updateObj(userAccount);
        uas.setAccountUser(userId);
        uas.setDealType(type.getCode());
        uas.setAmount(amount);
        uas.setUsableAmount(userAccount.getBalance());
        uas.setFreezedAmount(userAccount.getFreezedBalance());
        uas.setAccountType(type.getAccountType().getCode());
        uas.setAmountType(type.getArithmetic().getAmountType());
        uas.setRemark(type.getRemarkPre() + content + type.getRemarkSuf());
        Boolean inOrOut;
        if (type.getArithmetic().getUsable() != null) {
            inOrOut = type.getArithmetic().getUsable();
        }else {
            inOrOut = type.getArithmetic().getFreeze();
        }
        uas.setInOrOut(inOrOut);
        baseRepository.saveObj(uas);
    }

    @Override
    @Transactional
    public void updateUserAccount(String userId, BigDecimal amount, AccountStatementType type) {
        this.updateUserAccount(userId, amount, type,"");
    }

    //账户运算
    private UserAccount operationAccountAmount(String userId, BigDecimal amount,AccountStatementType type){
        UserAccount userAccount = getUserAccountByUserId(userId);
        AccountArithmetic arithmetic = type.getArithmetic();
        BigDecimal balance;
        BigDecimal freezedBalance;
        if (arithmetic.getUsable() != null){
            if (arithmetic.getUsable()){
                balance = userAccount.getBalance().add(amount);
            }else {
                if (AccountStatementType.CASH_APPLY.equals(type)){//如果是提现操作
                    BigDecimal maxCash = userAccount.getBalance().subtract(userAccount.getRefuseCash());
                    if (maxCash.compareTo(amount) < 0) {//且可提现金额不足
                        throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您提现最大金额：" + maxCash + "元");
                    }
                }else{
                    if (userAccount.getRefuseCash().compareTo(BigDecimal.ZERO) > 0) {//不可提现金额大于0时
                        BigDecimal refuse = userAccount.getRefuseCash().subtract(amount);
                        userAccount.setRefuseCash(refuse.compareTo(BigDecimal.ZERO) > 0 ? refuse : BigDecimal.ZERO);
                    }
                }
                balance = userAccount.getBalance().subtract(amount);
                if (balance.compareTo(BigDecimal.ZERO) < 0){//账户余额不足
                    throw new BusinessException(CommonErrorResult.BALANCE_DEFICIENCY,"余额不足");
                }
            }
            userAccount.setBalance(balance);
        }
        if (arithmetic.getFreeze() != null){
            if (arithmetic.getFreeze()) {
                freezedBalance = userAccount.getFreezedBalance().add(amount);
            }else {
                freezedBalance = userAccount.getFreezedBalance().subtract(amount);
                if (freezedBalance.compareTo(BigDecimal.ZERO) < 0){//冻结金额异常
                    throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"账户当前冻结金额：" + userAccount.getFreezedBalance() + "元");
                }
            }
            userAccount.setFreezedBalance(freezedBalance);
        }
        return userAccount;
    }

    @Override
    public CutPage<UserAccountStatement> queryUserAccountStatement(String userId, Integer amountType, Integer dealType, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(100);
        Map<String,Object> map = new HashMap<>();
        hql.append("from UserAccountStatement where accountUser = :userId");
        map.put("userId",userId);
        if (dealType != null){
            hql.append(" and dealType = :dealType");
            map.put("dealType",dealType);
        }
        if (amountType != null){
            hql.append(" and amountType = :amountType");
            map.put("amountType",amountType);
        }
        hql.append(" order by created desc");
        return baseRepository.executeHql(hql,map,page,limit);
    }

    /*
           金币转换余额
     */
    @Override
    @Transactional
    public void goldTransformationBalance(String userId, BigDecimal gold) {
        UserAccount userAccount = getUserAccountByUserId(userId);
        if (userAccount != null) {

            if (gold.floatValue() > userAccount.getGold().floatValue()) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您的金币不足！");
            }
            //减去转换的金币
            //userAccountDetailService.addUserAccountDetail(userId, AccountType.GOLD.getCode(), AccountStatus.COST.getCode(),gold);
            //增加余额
            //userAccountDetailService.addUserAccountDetail(userId, AccountType.BALANCE.getCode(), AccountStatus.INCOME.getCode(),gold);
            userAccount.setGold(userAccount.getGold().subtract(gold));
            userAccount.setBalance(userAccount.getBalance().add(gold));
            baseRepository.updateObj(userAccount);
        }
    }
}
