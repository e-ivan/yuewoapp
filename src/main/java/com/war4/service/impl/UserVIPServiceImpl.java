package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserVIP;
import com.war4.pojo.UserVipSetting;
import com.war4.repository.BaseRepository;
import com.war4.service.UserVIPService;
import com.war4.service.UserVipSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2016/12/26.
 */
@Service
public class UserVIPServiceImpl implements UserVIPService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private UserVipSettingService userVipSettingService;


    @Override
    @Transactional
    public void saveUserVIP(String fkUserId,String vip,Integer integral) {
        UserVIP userVIP = baseRepository.getObjById(UserVIP.class,fkUserId);
        if(userVIP==null){
            UserVIP insertObj = new UserVIP();
            insertObj.setFkUserId(fkUserId);
            insertObj.setVipSettingId(vip);
            insertObj.setIntegral(integral);
            baseRepository.saveObj(insertObj);
            return ;
        }
        userVIP.setFkUserId(vip);
        userVIP.setIntegral(integral);
        baseRepository.updateObj(vip);

    }

    @Override
    public UserVIP queryUserVipById(String fkUserId) {
        UserVIP userVIP = baseRepository.getObjById(UserVIP.class,fkUserId);
        return userVIP;
    }

    @Override
    @Transactional
    public void addIntegral(String fkUserId, Integer integral) {
        UserVIP userVIP = baseRepository.getObjById(UserVIP.class,fkUserId);
        if(userVIP==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在用户名！");
        }
        Integer total = userVIP.getIntegral()+integral;
        CutPage<UserVipSetting> cutPage = userVipSettingService.queryUserVipSetting();
        for(int i =0;i<cutPage.getiData().size();i++){
           //如果i+1 大于列表数据长度，会越界的
           if( i+1 <= cutPage.getiData().size() ){

               //如果用户累计消费金额大于等于 第i个 VIP的金额，小于 第i+1个 会员的等级金额
               //这个时候就是该用户就是 第i个 VIP 会员
               if ( total>=cutPage.getiData().get(i).getIntegral().intValue()  ){
                   userVIP.setVipSettingId(cutPage.getiData().get(i).getId());
               }

           }
            //判断是最后一个,如果用户累计消费金额大于等于 第i个 VIP的金额
            // 这个时候就是该用户就是 第i个 VIP 会员
            if( i+1 > cutPage.getiData().size() ){
                if ( total >= cutPage.getiData().get(i).getIntegral().intValue() ) {
                    userVIP.setVipSettingId(cutPage.getiData().get(i).getId());
                }
            }
        }
        userVIP.setIntegral(total);
        baseRepository.updateObj(userVIP);
    }

    @Override
    public CutPage<UserVIP> queryVIPListBySettingId(String setting) {
        String hql  = baseRepository.getBaseHqlByClass(UserVIP.class);
        hql += " and vipSettingId ='"+setting+"' ";
        CutPage<UserVIP> userVIPCutPage = baseRepository.queryCutPageData(hql,new CutPage<>(0,CutPage.MAX_COUNT)) ;
        return userVIPCutPage;
    }


}
