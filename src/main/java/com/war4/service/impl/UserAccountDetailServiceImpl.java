package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.enums.AccountStatus;
import com.war4.enums.AccountType;
import com.war4.enums.CommonDeleteFlag;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserAccountDetail;
import com.war4.repository.BaseRepository;
import com.war4.service.UserAccountDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Administrator on 2016/12/13.
 */
@Service
public class UserAccountDetailServiceImpl implements UserAccountDetailService {

    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public void addUserAccountDetail(String fkUserId, String accountType, Integer status, BigDecimal total) {

        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"用户Id不能为空！");
        }
        if(accountType==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"账户类型不能为空！");
        }
        if(status==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"进出账类型不能为空！");
        }
        if(AccountType.getByCode(accountType)==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到账户类型！");
        }
        if(AccountStatus.getByValue(status)==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到进出账类型！");
        }
        if(total==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"金额不能为空！");
        }
        if(total.floatValue()<=0.00f){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"金额不能为零");
        }

        UserAccountDetail userAccountDetail = new UserAccountDetail();
        userAccountDetail.setId(UUID.randomUUID().toString());
        userAccountDetail.setFkUserId(fkUserId);
        userAccountDetail.setAccountType(accountType);
        userAccountDetail.setStatus(status);
        userAccountDetail.setTotal(total);

        baseRepository.saveObj(userAccountDetail);


        //userAccountService.updateUserAccount(fkUserId,queryUserAccountForGold(fkUserId),queryUserAccountForBalance(fkUserId));
    }

    @Override
    public BigDecimal queryUserAccountForBalance(String userId) {
        String incomeSql = "SELECT COALESCE(SUM(total),0) FROM  UserAccountDetail WHERE del_flag = "
                + CommonDeleteFlag.NOT_DELETED.getCode()+" AND fk_user_id ='"+userId+"'AND  account_type ='"
                +AccountType.BALANCE.getCode()+"' AND STATUS ="+AccountStatus.INCOME.getCode()+" ";
        String incomeTotal = baseRepository.queryUniqueData(incomeSql).toString();
        if(incomeTotal==null){
            incomeTotal="0";
        }
        String costSql = "SELECT COALESCE(SUM(total),0) FROM  UserAccountDetail WHERE del_flag = "
                + CommonDeleteFlag.NOT_DELETED.getCode()+" AND fk_user_id ='"+userId+"'AND  account_type ='"
                +AccountType.BALANCE.getCode()+"' AND STATUS ="+AccountStatus.COST.getCode()+" ";
        String costTotal = baseRepository.queryUniqueData(costSql).toString();
        if(costTotal==null){
            costTotal="0";
        }

        return new BigDecimal(incomeTotal).subtract(new BigDecimal(costTotal));
    }

    @Override
    public BigDecimal queryUserAccountForGold(String userId) {
        String incomeSql = "SELECT COALESCE(SUM(total),0) FROM  UserAccountDetail WHERE del_flag = "
                + CommonDeleteFlag.NOT_DELETED.getCode()+" AND fk_user_id ='"+userId+"'AND  account_type ='"
                +AccountType.GOLD.getCode()+"' AND STATUS ="+AccountStatus.INCOME.getCode()+" ";
        String incomeTotal = baseRepository.queryUniqueData(incomeSql).toString();
        if(incomeTotal==null){
            incomeTotal="0";
        }
        String costSql = "SELECT COALESCE(SUM(total),0) FROM  UserAccountDetail WHERE del_flag = "
                + CommonDeleteFlag.NOT_DELETED.getCode()+" AND fk_user_id ='"+userId+"'AND  account_type ='"
                +AccountType.GOLD.getCode()+"' AND STATUS ="+AccountStatus.COST.getCode()+" ";
        String costTotal = baseRepository.queryUniqueData(costSql).toString();
        if(costTotal==null){
            costTotal="0";
        }

        return new BigDecimal(incomeTotal).subtract(new BigDecimal(costTotal));
    }

    @Override
    @Transactional
    public void addgold(String userId, BigDecimal glod) {
        addUserAccountDetail(userId, AccountType.GOLD.getCode(), AccountStatus.INCOME.getCode(),glod);
    }

}
