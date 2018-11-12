package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.SystemParameter;
import com.war4.repository.BaseRepository;
import com.war4.service.SystemParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2016/12/29.
 */
@Service
public class SystemParameterServiceImpl implements SystemParameterService {

    @Autowired
    private BaseRepository baseRepository;

    @Override
    public SystemParameter querySystemParameter() {
        String hql = baseRepository.getBaseHqlByClass(SystemParameter.class);
        SystemParameter systemParameter = baseRepository.queryUniqueData(hql);
        return systemParameter;
    }

    @Override
    @Transactional
    public void updateSystemParameter(String servicePhone, String aboutUs, String help) {
        SystemParameter systemParameter = querySystemParameter();
        if(systemParameter==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"未知错误");
        }
        if(servicePhone!=null){
            systemParameter.setServicePhone(servicePhone);
        }
        if(aboutUs!=null){
            systemParameter.setAboutUs(aboutUs);
        }
        if(help!=null){
            systemParameter.setHelp(help);
        }
        baseRepository.updateObj(systemParameter);
    }
}
