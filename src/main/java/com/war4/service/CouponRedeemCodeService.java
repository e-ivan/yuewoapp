package com.war4.service;

import com.war4.base.CutPage;
import com.war4.enums.CouponRedeemCodeType;
import com.war4.pojo.CouponRedeemCode;

/**
 * 代金券兑换码服务
 * Created by hh on 2017.9.18 0018.
 */
public interface CouponRedeemCodeService {
    /**
     * 创建兑换码
     * @param userId    管理员Id
     * @param mobile    手机号码
     * @param spilt     规则数据字典明细
     * @param type      类型  0：电影 1：商城
     * @param sendMsg   是否需要发送短信通知
     */
    String createRedeemCode(String userId, String mobile, Long spilt, CouponRedeemCodeType type, boolean sendMsg);

    /**
     * 兑换商城优惠券
     * @param userId
     * @param redeemCode
     */
    void exchangeRedeemCode(String userId, String redeemCode);

    //根据兑换码查询
    CouponRedeemCode getByRedeemCode(String redeemCode);

    CutPage<CouponRedeemCode> queryRedeemCodeList(String type, Integer state, Integer source, String userId, String keyword, Integer page, Integer limit);
}
