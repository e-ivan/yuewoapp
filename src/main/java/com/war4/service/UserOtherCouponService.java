package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.UserCoupon;
import com.war4.pojo.UserOtherCoupon;

/**
 * Created by Administrator on 2017/1/17.
 */
public interface UserOtherCouponService {

    CutPage<UserOtherCoupon> queryAllDatingUserOtherCouponByUserId(String userId, String[] couponIds);
    void addDatingUserCoupon(String fkUserId, String datingId);

}
