package com.war4.listener.event;

import com.war4.pojo.CouponRedeemCode;
import org.springframework.context.ApplicationEvent;

/**
 * 兑换码事件
 * Created by hh on 2017.9.18 0018.
 */
public class RedeemCodeEvent extends ApplicationEvent {
    private CouponRedeemCode redeemCode;

    public RedeemCodeEvent(CouponRedeemCode source) {
        super(source);
        this.redeemCode = source;
    }

    public CouponRedeemCode getRedeemCode() {
        return redeemCode;
    }
}
