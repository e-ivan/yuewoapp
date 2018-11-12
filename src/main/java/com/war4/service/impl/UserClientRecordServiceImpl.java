package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserClientRecord;
import com.war4.pojo.UserInfoBase;
import com.war4.service.UserClientRecordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.9.26 0026.
 */
@Service
public class UserClientRecordServiceImpl extends BaseService implements UserClientRecordService {
    @Override
    @Transactional
    public void saveUserClient(String userId, String imei, String model, String os, String rid) {
        if (StringUtils.isBlank(imei)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"imei不能为空");
        }
        if (StringUtils.isBlank(rid)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"rid不能为空");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户不存在");
        }
        user.setDeviceToken(rid);
        baseRepository.updateObj(user);
        UserClientRecord userClient = this.getUserClient(userId, imei);
        if (userClient != null){
            userClient.setLastLogin(new Date());
            userClient.setLogins(userClient.getLogins() + 1);
            baseRepository.updateObj(userClient);
            return;
        }
        userClient = new UserClientRecord();
        userClient.setModel(model);
        userClient.setLastLogin(new Date());
        userClient.setImei(imei);
        userClient.setOs(os);
        userClient.setUserId(userId);
        baseRepository.saveObj(userClient);
    }

    @Override
    public UserClientRecord getUserClient(String userId, String imei) {
        String hql = "from UserClientRecord where userId = :userId and imei = :imei";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("imei",imei);
        return baseRepository.executeHql(hql,map);
    }
}
