package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.PropertiesConfig;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.UserCorrelative;
import com.war4.pojo.UserInfoBase;
import com.war4.pojo.UserRecommend;
import com.war4.repository.BaseRepository;
import com.war4.service.UserCorrelativeService;
import com.war4.service.UserCouponService;
import com.war4.service.UserInfoBaseService;
import com.war4.service.UserRecommendService;
import com.war4.vo.PushMsgVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.8.7 0007.
 */
@Service
public class UserRecommendServiceImpl implements UserRecommendService,ApplicationContextAware {
    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private UserInfoBaseService userService;
    @Autowired
    private UserCorrelativeService correlativeService;
    @Autowired
    private UserCouponService couponService;

    private ApplicationContext ac;

    @Override
    @Transactional
    public void addReferrer(String userId, String recommendCode) {
        if (StringUtils.isBlank(userId)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空！");
        }
        UserCorrelative uc = correlativeService.queryUserCorrelativeByUserId(userId);
        if (uc.getInvites() > 0){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"您不能再接受别人的邀请!");
        }
        if (StringUtils.isBlank(recommendCode)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"推荐码不能为空！");
        }
        UserRecommend ur = getRecommendByUser(userId);
        if (ur != null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"你已接受推荐！");
        }
        //获取推荐码对应的用户
        UserInfoBase userInfoBase = userService.getUserByRecommendCode(recommendCode);
        if (userInfoBase == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"推荐码不存在！");
        }
        if (userInfoBase.getId().equals(userId)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"自己不能推荐自己哦！");
        }
        //设置用户推荐信息
        ur = new UserRecommend();
        ur.setCreated(new Date());
        ur.setUserId(userId);
        ur.setRecommendCode(recommendCode);
        ur.setReferrerId(userInfoBase.getId());
        baseRepository.saveObj(ur);
        if (StringUtils.isNotBlank(PropertiesConfig.getBeRecommendedCouponId())){
            couponService.addUserCoupon(userId, PropertiesConfig.getBeRecommendedCouponId(), 1);
            //推送事件
            PushMsgVO pmv = new PushMsgVO(userId,null,"接收推荐优惠券","", ur.getId(),MessageLogType.INVITE_USER);
            ac.publishEvent(new PushEvent(pmv));
        }
        //查询用户推荐人数
        UserCorrelative userCorrelative = correlativeService.queryUserCorrelativeByUserId(userInfoBase.getId());
        //用户推荐人数+1
        userCorrelative.setInvites(userCorrelative.getInvites() + 1);
        if (StringUtils.isNotBlank(PropertiesConfig.getRecommendCouponId()) && PropertiesConfig.getRecommendNum().equals(userCorrelative.getInvites())) {
            //当用户推荐人数达到指定值时，赠送优惠券
            couponService.addUserCoupon(userInfoBase.getId(), PropertiesConfig.getRecommendCouponId(), 1);
            //推送事件
            PushMsgVO pmv = new PushMsgVO(userInfoBase.getId(),null,"接收推荐优惠券","", ur.getId(),MessageLogType.INVITE_USER);
            ac.publishEvent(new PushEvent(pmv));
        }
        baseRepository.updateObj(userCorrelative);
    }

    @Override
    public UserRecommend getRecommendByUser(String userId) {
        if (StringUtils.isBlank(userId)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空！");
        }
        String hql = "from UserRecommend where userId = :userId";
        Map<String,Object> param = new HashMap<>();
        param.put("userId",userId);
        return baseRepository.executeHql(hql,param);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
