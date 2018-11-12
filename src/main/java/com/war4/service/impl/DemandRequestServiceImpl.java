package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.DemandRequest;
import com.war4.repository.BaseRepository;
import com.war4.service.DemandRequestService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hh on 2017.9.21 0021.
 */
@Service
public class DemandRequestServiceImpl implements DemandRequestService{
    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public void createDemandRequest(DemandRequest demandRequest) {
        if (StringUtils.isBlank(demandRequest.getMobile())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"请输入手机号码！");
        }
        DemandRequest dr = new DemandRequest();
        dr.setBudgetFund(demandRequest.getBudgetFund());
        dr.setContent(demandRequest.getContent());
        dr.setNickname(demandRequest.getNickname());
        dr.setMobile(demandRequest.getMobile());
        baseRepository.saveObj(dr);
    }
}
