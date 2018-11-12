package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.AccountStatementType;
import com.war4.enums.AuditStatus;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.Cash;
import com.war4.pojo.UserInfoBase;
import com.war4.repository.BaseRepository;
import com.war4.service.CashService;
import com.war4.service.UserAccountService;
import com.war4.service.UserCashAccountService;
import com.war4.util.BitStateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/15.
 */
@Service
public class CashServiceImpl implements CashService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserCashAccountService userCashAccountService;



    @Override
    @Transactional
    public void addCash(Cash cash) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, cash.getApplierId());
        if (user == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"用户不存在");
        }
        if (!user.getBindCashAccount()){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"请先绑定提现方式");
        }
        if (user.getHasCashProcess()){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"您有一单提现正在处理");
        }
        //处理账户数据
        userAccountService.updateUserAccount(cash.getApplierId(), cash.getAmount(), AccountStatementType.CASH_APPLY);
        Cash c = new Cash();
        c.setAccount(cash.getAccount());
        c.setAccountName(cash.getAccountName());
        c.setAccountType(cash.getAccountType());
        c.setAmount(cash.getAmount());
        c.setBankName(cash.getBankName());
        c.setApplierId(cash.getApplierId());
        c.setNote(cash.getNote());
        c.setStatus(AuditStatus.CREATE.getCode());
        baseRepository.saveObj(c);
        user.addState(BitStateUtil.OP_HAS_CASH_PROCESS);
        baseRepository.updateObj(user);
    }

    @Override
    @Transactional
    public void updateCashStatus(Cash cash, boolean agree) {
        Cash c = baseRepository.getObjById(Cash.class, cash.getId());
        if (c == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该笔提现记录！");
        }
        if (!AuditStatus.CREATE.getCode().equals(c.getStatus())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "该笔提现记录已经操作了！");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, c.getApplierId());
        if (user == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"用户不存在");
        }
        c.setStatus(agree ? AuditStatus.PASS.getCode() : AuditStatus.REFUSE.getCode());
        c.setRemark(cash.getRemark());
        c.setAuditTime(new Date());
        c.setAuditorId(cash.getAuditorId());
        baseRepository.updateObj(c);
        //处理账户数据
        userAccountService.updateUserAccount(c.getApplierId(),c.getAmount(),
                agree ? AccountStatementType.CASH_SUCCESSFUL : AccountStatementType.CASH_REFUSE);
        user.removeState(BitStateUtil.OP_HAS_CASH_PROCESS);
        baseRepository.updateObj(user);
    }

    @Override
    public CutPage<Cash> queryCashForUser(String fkUserId, Integer page, Integer limit) {
        if (StringUtils.isBlank(fkUserId)) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "用户id不存在");
        }
        String hql = "from Cash where delFlag = 0 and applierId = :userId ";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", fkUserId);
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public CutPage<Cash> queryCashForAll(Integer state,Integer accountType,String keyword, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(100);
        hql.append("from Cash where delFlag = 0 ");
        Map<String, Object> map = new HashMap<>();
        if (state != null) {
            hql.append(" and status = :state");
            map.put("state", state);
        }
        if (accountType != null){
            hql.append(" and accountType = :accountType");
            map.put("accountType",accountType);
        }
        if (StringUtils.isNotBlank(keyword)) {
            hql.append(" and (applierId in (select userId from UserCashAccount where (alipayAccount like :keyword or phone like :keyword or accountName like :keyword)) or applierId like :keyword)");
            map.put("keyword", "%" + keyword + "%");
        }
        hql.append(" order by createTime desc");
        CutPage<Cash> cutPage = baseRepository.executeHql(hql, map, page, limit);
        for (Cash cash : cutPage.getiData()) {
            cash.setCa(userCashAccountService.getCashAccountByUser(cash.getApplierId()));
        }
        return cutPage;
    }
}
