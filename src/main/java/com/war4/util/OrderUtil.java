package com.war4.util;


import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/16.
 */
public class OrderUtil {
    private OrderUtil() {
    }

    private static final String split = "T";
    public static final String KOU_DIAN_YING = "a";     //抠电影
    public static final String TIAN_ZHI = "t";          //天智创客
    public static final String ACTIVITY_BID = "y";      //一元夺票
    public static final String AUCTION_DEPOSIT = "d";   //竞拍押金
    public static final String AUCTION = "o";           //竞拍订单
    public static final String AUCTION_ITEM = "j";           //竞拍明细订单
    public static final String RECHARGE = "b";           //充值订单
    public static final String VIDEO_CHAT = "s";           //视频聊天订单


    public static String getUUID() {
        String str = String.valueOf(System.currentTimeMillis());
        int i = (int) (Math.random() * 100000);
        str += String.valueOf(i);
        return str;
    }

    public static String createOrderId(String type) {
        return type + getUUID();
    }

    /**
     * 生成天智创客首个id
     *
     * @return
     */
    public static long createYingPlanId() {
        //取当前时间的年月日
        String str = DateFormatUtils.format(new Date(), "yyMMdd") + "000000";
        return Long.parseLong(str);
    }

    /**
     * 生成天智创客首个id
     *
     * @return
     */
    public static long createYingMovieId() {
        //取当前时间的年月日
        String str = DateFormatUtils.format(new Date(), "yyMMddHHmmss");
        return Long.parseLong(str);
    }


    /**
     * 为订单添加由时间生成的后缀
     *
     * @param orderId
     */
    public static String addOrderSuffix(String orderId) {
        String suf = String.valueOf(System.currentTimeMillis()).substring(0, 10);
        return orderId + split + suf;
    }

    /**
     * 去除商户订单的后缀，生成原订单id
     *
     * @param outTradeNo
     * @return
     */
    public static String removeOrderSuffix(String outTradeNo) {
        int endIndex = outTradeNo.lastIndexOf(split);
        return outTradeNo.substring(0, endIndex < 0 ? outTradeNo.length() - 1 : endIndex);
    }

    public static String parseSeatInfo(String seatInfo) {
        String[] seat = seatInfo.split(",");
        StringBuilder seatName = new StringBuilder(20);
        for (int i = 0; i < seat.length; i++) {
            String[] num = seat[i].split("_");
            int length = num.length;
            if (length > 1) {
                seatName.append(num[length - 2]).append("排").append(num[length - 1]).append("座");
                if (i != seat.length - 1) {
                    seatName.append(",");
                }
            }
        }
        return seatName.toString();
    }

    public static void main(String args[]) {
        System.out.println(System.currentTimeMillis());
    }
}
