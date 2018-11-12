package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.UserShareRecordType;
import com.war4.pojo.UserInfoBase;
import com.war4.pojo.UserShareRecord;
import com.war4.service.UserShareRecordService;
import com.war4.util.DateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by E_Iva on 2018.1.4.0004.
 */
@Service
public class UserShareRecordServiceImpl extends BaseService implements UserShareRecordService {

    @Override
    @Transactional
    public void saveShare(String userId, String url, Integer type,String objectId) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在");
        }
        if (UserShareRecordType.getByCode(type) == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "分享类型不存在");
        }
        UserShareRecord share = new UserShareRecord();
        share.setUserId(userId);
        share.setObjectId(objectId);
        share.setType(type);
        share.setUrl(url);
        baseRepository.saveObj(share);
    }

    @Override
    public long checkShare(String userId, String objectId, Integer type) {
        String hql = "select count(id) from UserShareRecord where userId = :userId and type = :type and objectId = :objectId and created between :starDate and :endDate ";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("userId", userId);
        paramMap.put("type", type);
        paramMap.put("objectId", objectId);
        Date date = new Date();
        paramMap.put("starDate", DateUtils.truncate(date, Calendar.DATE));
        paramMap.put("endDate", DateUtil.endOfDay(date));
        return baseRepository.executeHql(hql, paramMap);
    }
}
