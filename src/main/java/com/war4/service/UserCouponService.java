package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.UserCoupon;

/**
 * Created by Administrator on 2017/1/17.
 */
public interface UserCouponService {
    void addUserCoupon(String fkUserId, String couponId, Integer count);
    UserCoupon queryUserCouponById(String id);
    void deleteUserCoupon(String userCouponId);
    void updateUserCouponUsed(String userCouponId);
    void updateUserCouponNotUse(String userCouponId);
    CutPage<UserCoupon> queryUserCouponByUserId(String userId, String status, Integer page, Integer limit);
    CutPage<UserCoupon> queryUserCouponByCouponId(String couponId, String userId, Integer page, Integer limit);
    //根据用户优惠券id和用户id查找
    UserCoupon queryUserCouponByUcIdAndUsId(String userCouponId,String userId);

}
