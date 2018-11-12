package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.Coupon;
import com.war4.pojo.UserCoupon;
import com.war4.vo.SuggestionVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/1/17.
 */
@Controller
@RequestMapping(value = "/coupon")
public class CouponController extends BaseController {


    //添加优惠券
    @RequestMapping(value = "addCoupon")
    public
    @ResponseBody
    Response addCoupon(Coupon coupon) {
        if (StringUtils.isNoneBlank(coupon.getId())) {
            couponService.updateCoupon(coupon);
            return new Response("修改成功");
        }else {
            couponService.addCoupon(coupon);
            return new Response("添加成功");
        }
    }

    //获取优惠券
    @RequestMapping(value = "getCoupon")
    public
    @ResponseBody
    Response getCoupon(String couponId) {
        Coupon coupon = couponService.getCoupon(couponId);
        return new ObjectResponse<>(coupon);
    }


    //修改优惠券
    @RequestMapping(value = "updateCoupon")
    public
    @ResponseBody
    Response updateCoupon(@ModelAttribute Coupon coupon) {
        couponService.updateCoupon(coupon);
        return new Response("修改成功");
    }

    //删除优惠券
    @RequestMapping(value = "delCoupon")
    public
    @ResponseBody
    Response delCoupon(String couponId) {
        couponService.delCoupon(couponId);
        return new Response("删除成功");

    }

    //恢复优惠券
    @RequestMapping(value = "restoreCoupon")
    public
    @ResponseBody
    Response restoreCoupon(String couponId) {
        couponService.restoreCoupon(couponId);
        return new Response("恢复成功");

    }

    //查询优惠券列表
    @RequestMapping(value = "queryCoupons")
    public
    @ResponseBody
    Response queryCoupons(boolean simple,String keyword,Integer delFlag,Integer page, Integer limit) {
        CutPage<Coupon> cutPage = couponService.queryCoupons(delFlag,keyword,page, limit);
        if (!simple) {
            return new ObjectResponse<>(cutPage);
        }
        List<SuggestionVO> collect = cutPage.getiData().stream().map(
                c -> new SuggestionVO(c.getName(),c.getId()))
                .collect(Collectors.toList());
        Map<String,Object> map = new HashMap<>();
        map.put("query",keyword);
        map.put("suggestions",collect);
        return new ObjectResponse<>(map);
    }

    //添加用户优惠券
    @RequestMapping(value = "addUserCoupon")
    public
    @ResponseBody
    Response addUserCoupon(String userId, String couponId,Integer count,String password) {
        super.checkPassword(password);
        userCouponService.addUserCoupon(userId, couponId,count);
        return new Response("添加成功");
    }

    //删除用户优惠券
    @RequestMapping(value = "delUserCoupon")
    public
    @ResponseBody
    Response delUserCoupon(String userCouponId) {
        userCouponService.deleteUserCoupon(userCouponId);
        return new Response("删除成功");
    }

    //查看个人优惠券
    @RequestMapping(value = "queryUserCouponByUserId")
    public
    @ResponseBody
    Response queryUserCouponByUserId(String userId, String isUse, Integer page, Integer limit) {
        CutPage cutPage = userCouponService.queryUserCouponByUserId(userId, isUse, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //优惠券使用情况
    @RequestMapping(value = "queryUserCouponByCouponId")
    public
    @ResponseBody
    Response queryUserCouponByCouponId(String couponId,String userId,Integer page, Integer limit) {
        CutPage<UserCoupon> cutPage = userCouponService.queryUserCouponByCouponId(couponId,userId, page, limit);
        if (StringUtils.isNoneBlank(userId)) {
            List<UserCoupon> coupons = cutPage.getiData();
            Set<String> ids = coupons.stream().map(UserCoupon::getFkCouponId).collect(Collectors.toSet());
            Map<String, Coupon> couponMap = baseRepository.queryObjMap(Coupon.class, ids);
            coupons.forEach(uc -> uc.setCoupon(couponMap.get(uc.getFkCouponId())));
        }
        return new ObjectResponse<>(cutPage);
    }


    @RequestMapping(value = "addDatingCoupon", method = RequestMethod.POST)
    public
    @ResponseBody
    Response addDatingCoupon(String userId, String datingId) throws Exception {
        addCache(userId + datingId);
        try {
            userOtherCouponService.addDatingUserCoupon(userId, datingId);
        } finally {
            removeCache(userId + datingId);
        }
        return new ObjectResponse<>("已赠送优惠券");
    }


    @RequestMapping(value = "deleteCouponAndAllUserCoupon", method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteCouponAndAllUserCoupon(String couponId,String password) throws Exception {
        checkPassword(password);
        couponService.deleteCouponAndAllUserCoupon(couponId);
        return new Response("已删除所有用户优惠券");
    }

}
