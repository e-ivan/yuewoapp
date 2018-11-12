package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.UserCouponStatus;
import com.war4.pojo.Coupon;
import com.war4.pojo.UserCorrelative;
import com.war4.pojo.UserCoupon;
import com.war4.service.AssembleService;
import com.war4.service.DatingFilmService;
import com.war4.service.UserCorrelativeService;
import com.war4.service.UserCouponService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Administrator on 2017/1/17.
 */
@Service
public class UserCouponServiceImpl extends BaseService implements UserCouponService {

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private UserCorrelativeService correlativeService;

    @Autowired
    DatingFilmService datingFilmService;

    @Override
    @Transactional
    public void addUserCoupon(String fkUserId, String couponId, Integer count) {
        Coupon coupon = baseRepository.getObjById(Coupon.class, couponId);
        if (coupon == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "优惠券不存在！");
        }
        for (int i = 0; i < count; i++) {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setId(UUID.randomUUID().toString());
            userCoupon.setFkCouponId(couponId);
            userCoupon.setFkUserId(fkUserId);
            userCoupon.setIsUse(UserCouponStatus.NOT_USE.getCode());
            userCoupon.setOffTime(DateUtils.addDays(new Date(),coupon.getPrescription()));
            baseRepository.saveObj(userCoupon);
        }
        //用户优惠券+1
        correlativeService.addCount(UserCorrelative.FILM_COUPONS,fkUserId);
    }

    @Override
    public UserCoupon queryUserCouponById(String id) {
        UserCoupon userCoupon = baseRepository.getObjById(UserCoupon.class, id);
        if (userCoupon != null) {
            userCoupon = assembleService.assembleObject(userCoupon);
        }
        return userCoupon;
    }

    @Override
    @Transactional
    public void deleteUserCoupon(String userCouponId) {
        UserCoupon userCoupon = baseRepository.getObjById(UserCoupon.class, userCouponId);
        if (userCoupon != null) {
            if (userCoupon.getIsUse().equals(1)) {
                baseRepository.logicDelete(userCoupon);
            }else {
                baseRepository.physicsDelete(userCoupon);
            }
            //用户优惠券-1
            correlativeService.reduceCount(UserCorrelative.FILM_COUPONS,userCoupon.getFkUserId());
        }
    }

    @Override
    @Transactional
    public void updateUserCouponUsed(String userCouponId) {
        UserCoupon userCoupon = baseRepository.getObjById(UserCoupon.class, userCouponId);
        if (userCoupon != null) {
            userCoupon.setIsUse(UserCouponStatus.USED.getCode());
            baseRepository.updateObj(userCoupon);
        }
    }

    @Override
    @Transactional
    public void updateUserCouponNotUse(String userCouponId) {
        UserCoupon userCoupon = baseRepository.getObjById(UserCoupon.class, userCouponId);
        if (userCoupon != null) {
            userCoupon.setIsUse(UserCouponStatus.NOT_USE.getCode());
            baseRepository.updateObj(userCoupon);
        }
    }

    @Override
    public CutPage<UserCoupon> queryUserCouponByUserId(String userId, String status, Integer page, Integer limit) {
        StringBuilder sb = new StringBuilder(200);
        sb.append("from UserCoupon where delFlag = 0 and fkUserId = :userId");
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("now",new Date());
        if (StringUtils.isNotEmpty(status)) {
            if ("yes".equals(status.toLowerCase())) {//已使用
                sb.append(" and isUse = 1");
            } else if ("no".equals(status.toLowerCase())) {//未使用，未过期
                sb.append(" and isUse = 0 and offTime >= :now ");
            } else if ("void".equals(status.toLowerCase())) {//未使用，已过期
                sb.append(" and isUse = 0 and offTime <= :now ");
            }else if ("past".equals(status.toLowerCase())){//已使用，已过期
                sb.append(" and (isUse = 1 or offTime <= :now) ");
            }
        }
        sb.append(" order by createTime desc, isUse ");
        CutPage<UserCoupon> cutPage = baseRepository.executeHql(sb.toString(), map, page, limit);
        for (UserCoupon userCoupon : cutPage.getiData()) {
            assembleService.assembleObject(userCoupon);
        }
        return cutPage;
    }

    @Override
    public CutPage<UserCoupon> queryUserCouponByCouponId(String couponId, String userId, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(200);
        hql.append("from UserCoupon where delFlag = 0 ");
        Map<String, Object> paramMap = baseRepository.paramMap();
        if (StringUtils.isNotBlank(couponId)) {
            hql.append(" and fkCouponId = :couponId ");
            paramMap.put("couponId",couponId);
        }
        if (StringUtils.isNotBlank(userId)){
            hql.append(" and fkUserId = :userId ");
            paramMap.put("userId",userId);
        }
        hql.append(" order by createTime desc");
        return baseRepository.executeHql(hql,paramMap,page,limit);
    }

    @Override
    public UserCoupon queryUserCouponByUcIdAndUsId(String userCouponId, String userId) {
        String hql = "from UserCoupon where delFlag = 0 and id = :userCouponId and fkUserId = :userId";
        Map<String,Object> map = new HashMap<>();
        map.put("userCouponId",userCouponId);
        map.put("userId",userId);
        UserCoupon userCoupon = baseRepository.executeHql(hql,map);
        assembleService.assembleObject(userCoupon);//封装数据
        return userCoupon;
    }

}
