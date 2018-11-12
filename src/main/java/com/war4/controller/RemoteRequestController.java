package com.war4.controller;

import com.war4.base.*;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.OrderType;
import com.war4.pojo.BaseOrder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 远程请求控制器
 * Created by hh on 2017.9.21 0021.
 */
@Controller
@RequestMapping(value = "remote")
public class RemoteRequestController extends BaseController {

    private static String remoteSecret = PropertiesConfig.getRemoteSecret();

    /**
     * 验证远程访问密钥
     * @param secret
     */
    private void checkRemoteSecret(String secret) {
        if (!remoteSecret.equals(secret)) {//验证不通过
            throw new BusinessException(CommonErrorResult.VERIFY_FAIL, "验证身份失败!");
        }
    }

    /**
     * 获取商城代金券信息
     *
     * @param couponId
     * @param secret   远程访问密钥
     */
    @RequestMapping(value = "getMarketCoupon")
    @ResponseBody
    public Response getMarketCoupon(String couponId, String secret) {
        this.checkRemoteSecret(secret);
        return new ObjectResponse<>(marketCouponService.getMarketCoupon(couponId));
    }

    /**
     * 更新商城代金券使用状态
     *
     * @param couponId
     * @param use      使用与否
     * @param secret   远程访问密钥
     */
    @RequestMapping(value = "updateMarketCouponState")
    @ResponseBody
    public Response updateMarketCouponState(String couponId, boolean use, String secret) {
        this.checkRemoteSecret(secret);
        marketCouponService.updateMarketCoupon(couponId, use);
        return new Response("更新成功!");
    }

    /**
     * 创建商城通用订单
     * @param secret   远程访问密钥
     */
    @RequestMapping(value = "createMarketBaseOrder")
    @ResponseBody
    public Response createMarketBaseOrder(String orderId, String secret) {
        this.checkRemoteSecret(secret);
        BaseOrder baseOrder = baseOrderService.queryBaseOrder(orderId);
        if (baseOrder != null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"订单号重复！");
        }
        baseOrderService.addBaseOrder(orderId, OrderType.MARKET_ORDER,null);
        return new Response("创建成功！");
    }

}
