package com.war4.service;

import com.war4.base.CutPage;
import com.war4.enums.MarketCouponSource;
import com.war4.pojo.MarketCoupon;

import java.math.BigDecimal;

/**
 * 商城优惠券服务
 * Created by hh on 2017.9.15 0015.
 */
public interface MarketCouponService {
    /**
     * 新增商城优惠券
     * @param userId    用户
     * @param valid     有效期
     * @param minUse    最低使用金额
     * @param source    来源
     */
    MarketCoupon createMarketCoupon(String userId, BigDecimal money, String name, int valid, BigDecimal minUse, MarketCouponSource source);

    /**
     * 查询用户商城代金券
     * @param userId
     * @param price     比较价格
     * @param ifPast    是否失效
     * @param source    来源
     * @param orderBy   排序方式
     * @param page
     * @param limit
     */
    CutPage<MarketCoupon> queryMarketCoupon(String userId, BigDecimal price, boolean ifPast, Integer source, String orderBy, Integer page, Integer limit);

    /**
     * 获取代金券信息
     * @param couponId
     */
    MarketCoupon getMarketCoupon(String couponId);

    /**
     * 更新代金券状态
     * @param couponId
     * @param use       使用与否
     */
    void updateMarketCoupon(String couponId,boolean use);
}
