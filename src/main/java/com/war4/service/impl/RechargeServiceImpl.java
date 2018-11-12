package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.base.SystemParameters;
import com.war4.enums.*;
import com.war4.pojo.Recharge;
import com.war4.pojo.UserAccount;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2016/12/14.
 */
@Service
public class RechargeServiceImpl implements RechargeService {

    public Logger logger = Logger.getLogger(FilmRoomServiceImpl.class.getName());

    @Autowired
    private PushPayloadService pushPayloadService;

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private UserAccountDetailService userAccountDetailService;

    @Autowired
    private UserVIPService userVIPService;

    @Autowired
    private BaseOrderService baseOrderService;

    @Override
    @Transactional
    public Recharge addRecharge(String fkUserId, BigDecimal money) {
        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"充值人Id不能为空");
        }
        if(money==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"充值金额不能为空");
        }
        if(money.floatValue()<=0.00f){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"充值金额不能为负数");
        }
        Recharge recharge = new Recharge();
        recharge.setId(OrderUtil.getUUID());
        recharge.setFkUserId(fkUserId);
        recharge.setMoney(money);
        recharge.setStatus(RechargeStatus.CREATE.getCode());
        baseRepository.saveObj(recharge);

        baseOrderService.addBaseOrder(recharge.getId(), OrderType.RECHARGE,fkUserId);
        UserAccount userAccount=baseRepository.getObjById(UserAccount.class,fkUserId);
        userAccount.setGold(userAccount.getGold().add(money));
        baseRepository.updateObj(userAccount);
        return recharge;
    }

    @Override
    public CutPage<Recharge> queryRechargeListByUserId(String fkUserId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(Recharge.class);
        hql +=" and fkUserId ='"+fkUserId+"'";
        CutPage<Recharge> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        return cutPage;
    }

    @Override
    public void updateSetRechargeSuccess(String rechargeId) {
        Recharge recharge = baseRepository.getObjById(Recharge.class,rechargeId);
        if(recharge==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到该笔充值记录！");
        }
        if(!recharge.getStatus().equals(RechargeStatus.CREATE.getCode())){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该笔充值状态不允许修改！");
        }
        userAccountDetailService.addUserAccountDetail(recharge.getFkUserId(), AccountType.GOLD.getCode(), AccountStatus.INCOME.getCode(),recharge.getMoney());

        recharge.setStatus(RechargeStatus.PAY_SUCCESS.getCode());
        //添加累计消费
        userVIPService.addIntegral(recharge.getFkUserId(),recharge.getMoney().intValue());

        baseRepository.updateObj(recharge);

        try {
            pushPayloadService.addPushPayloadForUserName(PropertiesConfig.getJpushAppMasterSecret(), PropertiesConfig.getJpushAppkey(), recharge.getFkUserId(), SystemParameters.MESSAGE_RECHARGE);
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
    @Override
    public Recharge queryRecharge(String orderId){
        Recharge recharge = baseRepository.getObjById(Recharge.class,orderId);
        return recharge;
    }
}
