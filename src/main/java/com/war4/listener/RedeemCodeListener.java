package com.war4.listener;

import com.war4.base.SystemParameters;
import com.war4.enums.CouponRedeemCodeType;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.RedeemCodeEvent;
import com.war4.pojo.CouponRedeemCode;
import com.war4.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 兑换码监听器
 * Created by hh on 2017.9.18 0018.
 */
@Component
public class RedeemCodeListener implements ApplicationListener<RedeemCodeEvent> {
    @Autowired
    private SmsService smsService;

    @Async
    @Override
    @Transactional
    public void onApplicationEvent(RedeemCodeEvent redeemCodeEvent) {
        CouponRedeemCode redeemCode = redeemCodeEvent.getRedeemCode();
        //发送短信信息
        String preMsg = "";
        MessageLogType logType = null;
        if (CouponRedeemCodeType.MARKET.getCode().equals(redeemCode.getType())) {
            preMsg = SystemParameters.REDEEM_CODE_MARKET_MSG_PRE;
            logType = MessageLogType.MARKER_COUPON;
        }else if (CouponRedeemCodeType.FILM.getCode().equals(redeemCode.getType())) {
            preMsg = SystemParameters.REDEEM_CODE_FILM_MSG_PRE;
            logType = MessageLogType.FILM_COUPON;
        }
        smsService.sendAndSaveSMS(preMsg + redeemCode.getRedeemCode() + SystemParameters.REDEEM_CODE_MSG_SUF,redeemCode.getMobile(), logType,null,redeemCode.getId().toString());
    }
}
