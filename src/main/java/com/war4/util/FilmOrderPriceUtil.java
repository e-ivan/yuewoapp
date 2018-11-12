package com.war4.util;

import com.war4.pojo.Coupon;
import com.war4.pojo.FilmOrder;
import com.war4.service.YinghezhongService;
import com.war4.vo.film.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 电影订单价格工具类
 * Created by hh on 2017.9.23 0023.
 */
@Component
public class FilmOrderPriceUtil {
    private static YinghezhongService yinghezhongService;

    @Autowired
    public void setYinghezhongService(YinghezhongService yinghezhongService) {
        FilmOrderPriceUtil.yinghezhongService = yinghezhongService;
    }

    private static final double lowestFee = 3D;//最低附加费

    /**
     * 计算场次电影销售价格
     *
     * @param plan
     * @return
     */
    public static Double countFilmSalePrice(YingCinemaPlayVO plan) {
        //获取最低服务费
        double serviceFee = 0D;
        List<YingAreaInfoVO> areaInfo = plan.getAreaInfo();
        if (areaInfo != null && areaInfo.size() > 0) {
            double min = 10000D;//初始化一个最小值
            for (YingAreaInfoVO _area : areaInfo) {
                Double service = _area.getAreaServiceFee();
                double ret = service == null ? 0 : service;
                if (ret < min) {
                    //如果比最小值小
                    min = ret;
                }
            }
            serviceFee = min;
        }
        //计算附加费
        double totalFee = countCinemaHandleFee(plan.getCinemaId()) + serviceFee;
        //总附加费低于最低附加费时使用最低附加费,否则使用总附加费
        return totalFee < lowestFee ? plan.getLowestPrice() + lowestFee : plan.getLowestPrice() + totalFee;
    }

    /**
     * 计算并设置订单价格
     *
     * @param filmOrder
     * @param seatNo
     * @param seatLock  @return
     */
    public static FilmOrder countFilmOrderMoney(FilmOrder filmOrder, YingCinemaPlayVO plan, String seatNo, YingSeatLockVO seatLock) {
        /*
            计算影票价格
            计算规则，计算每张票的价格，算总价
            1、单张影票的价格使用鼎新平台传回来的场次的lowestPrice
                如果在锁定位置后返回的数据中鼎新电商平台启用算出的价格(partnerPrice)不为空，则价格取partnerPrice的价格
            2、影票的手续费：
                每张影票收取一份手续费，手续费在影院配置信息cinemaConfig中cineHandleFee和handleFee的和，
                如果cineHandleFee字段不为空则下单时服务费不低于此价
            3、影票的服务费：
                在锁定座位后返回来的seatLock中areaInfo集合对应着服务费，其中会有两种情况
                第一种：集合为空集，则说明不需要服务费
                第二种：集合不为空，则集合会对应座位中的服务费，使用集合中的areaServiceFee服务费
            4、订单中的价格：
                unitPrice：影票单价，取1中的价格，不包含手续费、服务费
                handleFee：订单中的手续费，取2中的手续费 * 影票数量
                serviceFee：订单中的服务费，取3中的第二种情况集合中的和
                otherFee：订单中的其它费用，取handleFee和serviceFee的和，用（最低附加费 * 影票数量）与其相减，取大于0的差值
                money：订单总价，unitPrice * 影票数量 + handleFee + serviceFee + otherFee
                reducePrice：优惠金额，对应使用的优惠券中的价格
                payMoney：用户支付价格，money - reducePrice
         */
        //创建购买座位信息
        String[] seatNos = seatNo.split(",");
        List<YingBuySeatVO> seats = new ArrayList<>();
        double handleFee = countCinemaHandleFee(plan.getCinemaId());
        List<YingAreaInfoVO> areaInfos = seatLock.getAreaInfo();
        double totalHandleFee = 0D; //订单手续费
        double totalServiceFee = 0D; //订单服务费
        double totalOtherFee = 0D; //订单其他费
        double uniPrice = plan.getLowestPrice();//影票单价

        //鼎新电商平台启用算出的价格
        Double partnerPrice = seatLock.getPartnerPrice();
        if (partnerPrice != null && partnerPrice > 0) {
            uniPrice = partnerPrice;
            if (areaInfos.size() > 0) {
                Double areaPrice = areaInfos.get(0).getAreaPrice();
                if (areaPrice != null && areaPrice > 0) {
                    Double totalAreaPrice = 0D;
                    for (YingAreaInfoVO areaInfo : areaInfos) {
                        totalAreaPrice += areaInfo.getAreaPrice() != null ? areaInfo.getAreaPrice() : 0;
                    }
                    uniPrice = totalAreaPrice / areaInfos.size();
                }
            }
        }

        //每张影票的金额
        for (int i = 0; i < seatNos.length; i++) {
            //计算附加费
            double areaServiceFee = 0D;
            if (seatNos.length == areaInfos.size()) {
                Double asf = areaInfos.get(i).getAreaServiceFee();
                areaServiceFee = asf == null ? 0 : asf;
            }
            double dv = lowestFee - (handleFee + areaServiceFee);//差值
            totalHandleFee += handleFee;
            totalServiceFee += areaServiceFee;
            totalOtherFee += dv > 0 ? dv : 0;//计算其他费

            YingBuySeatVO seat = new YingBuySeatVO();
            seat.setSeatId(seatNos[i]);
            seat.setHandleFee(handleFee);
            seat.setPrice(uniPrice + areaServiceFee);
            seat.setUseRealPayPrice(uniPrice);
            seat.setServiceFee(areaServiceFee);
            seats.add(seat);
        }

        filmOrder.setUnitPrice(new BigDecimal(uniPrice));
        filmOrder.setHandleFee(new BigDecimal(totalHandleFee));
        filmOrder.setServiceFee(new BigDecimal(totalServiceFee));
        filmOrder.setOtherFee(new BigDecimal(totalOtherFee));
        filmOrder.setMoney(filmOrder.getUnitPrice().multiply(new BigDecimal(filmOrder.getCount()))
                .add(filmOrder.getServiceFee()).add(filmOrder.getHandleFee()).add(filmOrder.getOtherFee()));
        filmOrder.setPayMoney(filmOrder.getMoney());
        seatLock.setSeats(seats);
        return filmOrder;
    }

    /**
     * 计算电影订单中每个座位的价格
     */
    private static BigDecimal countFilmOrderOneSeatPrice(FilmOrder filmOrder){
       //获取uniPrice + 所有服务费 / 票数
        BigDecimal serviceFee = filmOrder.getHandleFee().add(filmOrder.getOtherFee()).add(filmOrder.getServiceFee());
        BigDecimal oneService = serviceFee.divide(new BigDecimal(filmOrder.getCount()), 2, BigDecimal.ROUND_UP);
        return filmOrder.getUnitPrice().add(oneService);
    }

    /**
     * 计算影院单张票的手续费
     *
     * @param cinemaId
     * @return
     */
    private static double countCinemaHandleFee(String cinemaId) {
        //获取手续费
        YingCinemaConfigVO cinemaConfig = yinghezhongService.getCinemaConfig(cinemaId);
        Double handleFee = cinemaConfig.getHandleFee();
        Double cineHandleFee = cinemaConfig.getCineHandleFee();
        //计算总手续费
        return (handleFee == null ? 0D : handleFee) + (cineHandleFee == null ? 0D : cineHandleFee);
    }

    /**
     * 计算电影订单优惠金额
     */
    public static BigDecimal countFilmOrderReducePrice(FilmOrder filmOrder,Coupon coupon){
        //普通类型：订单总金额 - 优惠券金额
        //通用类型：订单总金额 - 优惠券座位数 * 座位单价
        if (Coupon.COMMON.equals(coupon.getType())){
            //优惠座位数
            Integer discountSeats = filmOrder.getCount() >= coupon.getSeats() ? coupon.getSeats() : filmOrder.getCount();
            return FilmOrderPriceUtil.countFilmOrderOneSeatPrice(filmOrder).multiply(new BigDecimal(discountSeats));
        }else if (Coupon.NORMAL.equals(coupon.getType())){
            return coupon.getMoney();
        }
        return BigDecimal.ZERO;
    }
}
