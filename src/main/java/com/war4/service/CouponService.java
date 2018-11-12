package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.Coupon;

import java.util.List;

/**
 * Created by Administrator on 2017/1/17.
 */
public interface CouponService {
    Coupon addCoupon(Coupon coupon);
    Coupon updateCoupon(Coupon coupon);
    Coupon getCoupon(String couponId);
    void delCoupon(String couponId);
    void restoreCoupon(String couponId);
    CutPage<Coupon> queryCoupons(Integer delFlag, String keyword, Integer page, Integer limit);
    List<Integer> queryCouponMoneys(String[] couponIds);

    /**
     * 完全删除优惠券及其拥有其优惠券的用户优惠券
     * @param couponId
     */
    void deleteCouponAndAllUserCoupon(String couponId);

}
