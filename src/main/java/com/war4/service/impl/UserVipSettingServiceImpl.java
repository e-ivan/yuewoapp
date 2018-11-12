package com.war4.service.impl;

//import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonDeleteFlag;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserVipSetting;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.UserVipSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2017/1/6.
 */
@Service
public class UserVipSettingServiceImpl implements UserVipSettingService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public void addUserVipSetting(UserVipSetting userVipSetting) {
        if(userVipSetting.getIntegral()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该会员累计消费值不能为空");
        }
        if(userVipSetting.getVipName()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该会员名称不能为空");
        }
        if(userVipSetting.getIntro()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该会员特权说明不能为空");
        }
        if(userVipSetting.getFkCouponId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该会员赠送优惠券不能为空");
        }
        userVipSetting.setId(UUID.randomUUID().toString());
        baseRepository.saveObj(userVipSetting);
    }

    @Override
    @Transactional
    public void updateUserVipSetting(UserVipSetting userVipSetting) {
        if(userVipSetting.getId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该会员唯一标识不能为空");
        }
        if(userVipSetting.getIntegral()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该会员累计消费值不能为空");
        }
        if(userVipSetting.getVipName()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该会员名称不能为空");
        }
        if(userVipSetting.getIntro()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该会员特权说明不能为空");
        }
        if(userVipSetting.getFkCouponId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该会员赠送优惠券不能为空");
        }
        userVipSetting.setDelFlag(CommonDeleteFlag.NOT_DELETED.getCode());
        baseRepository.updateObj(userVipSetting);
    }

    @Override
    @Transactional
    public void deleteUserVipSetting(String settingId) {
        UserVipSetting userVipSetting = baseRepository.getObjById(UserVipSetting.class,settingId);
        if(userVipSetting!=null){
            baseRepository.logicDelete(userVipSetting);
        }
    }

    @Override
    public CutPage<UserVipSetting> queryUserVipSetting() {
        String hql = baseRepository.getBaseHqlByClass(UserVipSetting.class);
        hql += " ORDER BY integral ASC";
        CutPage<UserVipSetting> cutPage = new CutPage<>(0,CutPage.MAX_COUNT);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(UserVipSetting userVipSetting:cutPage.getiData()){
            userVipSetting = assembleService.assembleObject(userVipSetting);
        }
        return cutPage;
    }
}
