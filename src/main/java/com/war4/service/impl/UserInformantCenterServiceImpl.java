package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserInformantCenter;
import com.war4.repository.BaseRepository;
import com.war4.service.UserInformantCenterService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.9.8 0008.
 */
@Service
public class UserInformantCenterServiceImpl implements UserInformantCenterService {
    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public void createInformant(UserInformantCenter userInformantCenter) {
        if (StringUtils.isBlank(userInformantCenter.getInformer())) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "举报用户id不能为空");
        }
        if (StringUtils.isBlank(userInformantCenter.getDefendant())) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "被举报用户id不能为空");
        }
        if (StringUtils.isBlank(userInformantCenter.getContent())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "举报内容不能为空！");
        }
        if (queryInformant(userInformantCenter.getInformer(), userInformantCenter.getDefendant(), userInformantCenter.getType()) != null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "你已举报过该用户！");
        }

        UserInformantCenter informant = new UserInformantCenter();
        informant.setInformer(userInformantCenter.getInformer());
        informant.setDefendant(userInformantCenter.getDefendant());
        informant.setContent(userInformantCenter.getContent());
        informant.setEmail(userInformantCenter.getEmail());
        informant.setPhone(userInformantCenter.getPhone());
        informant.setQq(userInformantCenter.getQq());
        informant.setState(UserInformantCenter.CREATE);
        informant.setType(userInformantCenter.getType());
        baseRepository.saveObj(informant);
        //TODO 添加记录
    }

    @Override
    public UserInformantCenter queryInformant(String informer,String defendant,int type) {
        String hql = "from UserInformantCenter where type = :type and informer = :informer and defendant = :defendant";
        Map<String,Object> map = new HashMap<>();
        map.put("type",type);
        map.put("informer",informer);
        map.put("defendant",defendant);
        return baseRepository.executeHql(hql, map);
    }
}
