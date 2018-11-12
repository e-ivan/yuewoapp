package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.UserCorrelative;
import com.war4.pojo.UserFeedback;
import com.war4.repository.BaseRepository;
import com.war4.service.UserCorrelativeService;
import com.war4.service.UserFeedbackService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by hh on 2017.8.2 0002.
 */
@Service
public class UserFeedbackServiceImpl implements UserFeedbackService {
    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private UserCorrelativeService correlativeService;

    @Override
    @Transactional
    public void createFeedback(UserFeedback userFeedback) {
        if (StringUtils.isBlank(userFeedback.getUserId())){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空");
        }
        if (StringUtils.isBlank(userFeedback.getContent())){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"反馈内容不能为空！");
        }

        UserFeedback feedback = new UserFeedback();
        feedback.setUserId(userFeedback.getUserId());
        feedback.setContent(userFeedback.getContent());
        feedback.setCreated(new Date());
        feedback.setEmail(userFeedback.getEmail());
        feedback.setPhone(userFeedback.getPhone());
        feedback.setQq(userFeedback.getQq());
        feedback.setState(UserFeedback.CREATE);
        feedback.setType(userFeedback.getType());
        baseRepository.saveObj(feedback);
        correlativeService.addCount(UserCorrelative.FEED_BACKS,userFeedback.getUserId());//用户反馈+1
    }
}
