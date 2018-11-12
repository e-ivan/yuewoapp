package com.war4.controller;

import com.war4.base.*;
import com.war4.pojo.MarketCoupon;
import com.war4.service.MarketCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 商城代金券控制器
 * Created by hh on 2017.9.19 0019.
 */
@Controller
@RequestMapping(value = "marketCoupon")
public class MarketCouponController extends BaseController {


    /**
     * 根据订单价格查询代金券
     *
     * @param userId
     * @param price
     */
    @RequestMapping(value = "queryMarketCouponByPrice", method = RequestMethod.POST)
    @ResponseBody
    public Response queryMarketCouponByPrice(String userId, BigDecimal price, Integer page, Integer limit) {
        return new ObjectResponse<>(marketCouponService.queryMarketCoupon(userId, price, false, null, "minUse", page, limit));
    }

    /**
     * 获取最优方案
     *
     * @param userId
     * @param price
     */
    @RequestMapping(value = "getOptimalMarketCoupon", method = RequestMethod.POST)
    @ResponseBody
    public Response getOptimalMarketCoupon(String userId, BigDecimal price) {
        CutPage<MarketCoupon> cutPage = marketCouponService.queryMarketCoupon(userId, price, false, null, "optimal", 0, 1);
        MarketCoupon marketCoupon = cutPage.getiData().isEmpty() ? null : cutPage.getiData().get(0);
        Map<String, Object> map = new HashMap<>();
        if (marketCoupon != null) {
            map.put("discountMoney", marketCoupon.getMoney());
            map.put("couponId", marketCoupon.getCouponId());
        }
        return new ObjectResponse<>(map);
    }

    /**
     * 根据状态获取商城代金券
     *
     * @param userId
     * @param ifPast
     * @param page
     * @param limit
     */
    @RequestMapping(value = "queryMarketCoupon", method = RequestMethod.POST)
    @ResponseBody
    public Response queryMarketCoupon(String userId, boolean ifPast, Integer page, Integer limit) {
        return new ObjectResponse<>(marketCouponService.queryMarketCoupon(userId, null, ifPast, null, "offTime", page, limit));
    }



}
