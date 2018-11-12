package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserInfoBase;
import com.war4.pojo.UserRatio;
import com.war4.service.UserRatioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by E_Iva on 2018.1.22.0022.
 */
@Service
public class UserRatioServiceImpl extends BaseService implements UserRatioService {
    @Override
    @Transactional
    public void save(UserRatio ratio) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, ratio.getUserId());
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"用户不存在");
        }
        if (ratio.getAuctionRatio().compareTo(BigDecimal.ZERO) < 0 || ratio.getVideoChatRatio().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"比例不能小于0");
        }
        if (ratio.getAuctionRatio().compareTo(BigDecimal.ONE) > 0 || ratio.getVideoChatRatio().compareTo(BigDecimal.ONE) > 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"比例不能大于1");
        }
        UserRatio latestByUser = this.getLatestByUser(ratio.getUserId());
        if (latestByUser == null || latestByUser.getVideoChatRatio().compareTo(ratio.getVideoChatRatio()) != 0 ||
                latestByUser.getAuctionRatio().compareTo(ratio.getAuctionRatio()) != 0) {
            //有一个不相同则保存新纪录
            baseRepository.saveObj(ratio);
        }
    }

    @Override
    public UserRatio getLatestByUser(String userId) {
        String hql = "from UserRatio where userId = :userId order by created desc";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("userId",userId);
        return baseRepository.executeHql(hql,paramMap);
    }

}
