package com.war4.listener;

import com.war4.base.PropertiesConfig;
import com.war4.enums.MarketCouponSource;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.FilmOrderEvent;
import com.war4.pojo.FilmOrder;
import com.war4.pojo.MarketCoupon;
import com.war4.pojo.UserCorrelative;
import com.war4.repository.BaseRepository;
import com.war4.service.MarketCouponService;
import com.war4.service.SmsService;
import com.war4.service.UserCorrelativeService;
import com.war4.util.OrderUtil;
import com.war4.vo.film.CinemaPlanVO;
import com.war4.vo.film.FilmOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 电影订单事件监听器
 * Created by hh on 2017.8.15 0015.
 */
@Component
@Slf4j
public class FilmOrderListener implements ApplicationListener<FilmOrderEvent> {
    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private SmsService smsService;
    @Autowired
    private MarketCouponService marketCouponService;
    @Autowired
    private UserCorrelativeService correlativeService;

    @Async
    @Override
    @Transactional
    public void onApplicationEvent(FilmOrderEvent filmOrderEvent) {
        String orderId = null;
        try {
            FilmOrderVO orderVO = filmOrderEvent.getFilmOrderVO();
            orderId = orderVO.getOrderId();//订单号
            CinemaPlanVO plan = orderVO.getPlan();
            //编辑短信内容
            StringBuilder sms = new StringBuilder(200);
            sms.append("使用验证码[").append(orderVO.getFinalVerifyCode()).append("]到").append(orderVO.getMachineType()).append("取票。")
                    .append("影票信息：[").append(plan.getCinema().getCinemaName()).append("]")
                    .append(DateFormatUtils.format(plan.getFeatureTimeDate(), "M月d日 HH:mm")).append(" [").append(plan.getMovie().getMovieName()).append("]")
                    .append(plan.getHallName()).append(" ").append(OrderUtil.parseSeatInfo(orderVO.getSeatInfo()));
            //获取订单信息
            FilmOrder order = baseRepository.getObjById(FilmOrder.class, orderId);
            String userId = null;
            if (order != null) {
                userId = order.getFkUserId();
                //赠送商城现金券
                giveMarketCoupon(order);
            }
            //发送并保存用户短信信息
            smsService.sendAndSaveSMS(sms.toString(), orderVO.getMobile(), MessageLogType.FILM_ORDER, userId, orderId);
        } catch (Exception e) {
            log.error("电影订单事件执行失败 orderId {}，原因 {}",orderId,e.getLocalizedMessage());
        }

    }

    @Transactional
    private void giveMarketCoupon(FilmOrder order) {
        BigDecimal payMoney = order.getPayMoney().setScale(0, RoundingMode.HALF_UP);
        if (payMoney.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal money;
            if (payMoney.remainder(BigDecimal.TEN).compareTo(BigDecimal.ZERO) == 0
                    || payMoney.remainder(new BigDecimal("5")).compareTo(BigDecimal.ZERO) == 0) {//整除10或整除5
                money = payMoney;
            } else {
                //8折后四舍五入
                money = payMoney.multiply(new BigDecimal("0.8")).setScale(0, RoundingMode.HALF_UP);
            }
            String msg = "购票成功，已将" + money + "元约我商城代金券发放至您的账户，请留意。感谢您的支持！";
            MarketCoupon mc = marketCouponService.createMarketCoupon(order.getFkUserId(), money, PropertiesConfig.getFilmOrderMarketCouponName(), PropertiesConfig.getMarketCouponNameValid(), money.add(new BigDecimal("0.1")), MarketCouponSource.FILM_ORDER);
            //用户商城优惠券+1
            correlativeService.addCount(UserCorrelative.MARKET_COUPONS, order.getFkUserId());
            //发送并保存用户短信信息
            smsService.sendAndSaveSMS(msg, order.getMobile(), MessageLogType.MARKER_COUPON, order.getFkUserId(), mc.getCouponId());
        }
    }
}
