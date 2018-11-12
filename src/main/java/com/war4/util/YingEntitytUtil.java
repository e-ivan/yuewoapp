package com.war4.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.war4.vo.film.*;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * 鼎新电影实体
 * Created by E_Iva on 2018.1.16.0016.
 */
@Component
public class YingEntitytUtil {
    private YingEntitytUtil() {
    }

    /**
     * 获取平台支持影院列表
     */
    public static List<YingCinemaVO> partnerCinemas() {
        TreeMap<String, String> map = new TreeMap<>();
        return JSONArray.parseArray(RequestUtil.baseYingRequest("/partner/cinemas", map), YingCinemaVO.class);
    }

    /**
     * 获取影院场次，获取总天数
     *
     * @param cid
     * @param amountDay 获取天数
     */
    public static List<YingCinemaPlayVO> cinemaPlays(String cid, Integer amountDay) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("cid", cid);
        if (amountDay != null) {
            Date date = new Date();
            map.put("start", DateFormatUtils.format(date, "yyyy-MM-dd"));
            map.put("end", DateFormatUtils.format(DateUtils.addDays(date, amountDay), "yyyy-MM-dd"));
        }
        return JSONArray.parseArray(RequestUtil.baseYingRequest("/cinema/plays", map), YingCinemaPlayVO.class);
    }

    /**
     * 获取影院场次，默认当天早上6:00:00到次天5:59:59的场次
     *
     * @param cid
     */
    public static List<YingCinemaPlayVO> cinemaPlays(String cid) {
        return cinemaPlays(cid, null);
    }

    /**
     * 获取影院影厅列表
     *
     * @param cid
     */
    public static List<YingCinemaHallVO> cinemaHalls(String cid) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("cid", cid);
        return JSONArray.parseArray(RequestUtil.baseYingRequest("/cinema/halls",map), YingCinemaHallVO.class);
    }

    /**
     * 座位锁定
     */
    public static YingSeatLockVO seatLock(String cid, String playId, String playUpdateTime, String seatId) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("cid", cid);
        map.put("play_id", playId);
        map.put("play_update_time", playUpdateTime);
        map.put("seat_id", seatId);
        String text = RequestUtil.baseYingRequest("/seat/lock", map);
        System.out.println("text = " + text);
        return JSONObject.parseObject(text, YingSeatLockVO.class);
    }

    /**
     * 座位解锁
     *
     */
    public static void seatUnlock(String cid, String playId, String lockFlag, String seatId) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("cid", cid);
        map.put("play_id", playId);
        map.put("seat_id", seatId);
        map.put("lock_flag", lockFlag);
        RequestUtil.baseYingRequest("/seat/unlock", map);
    }

    //锁定座位后购买
    public static YingTicketVO seatLockBuy(String cid, String playId, String playUpdateTime, String seat, String lockFlag, String partnerBuyTicketId, String mobile) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("cid", cid);
        map.put("play_id", playId);
        map.put("seat", seat);
        map.put("lock_flag", lockFlag);
        map.put("play_update_time", playUpdateTime);
        map.put("partner_buy_ticket_id", partnerBuyTicketId);
        map.put("mobile", mobile);
        return JSONArray.parseObject(RequestUtil.baseYingRequest("/seat/lock-buy", map), YingTicketVO.class);
    }

    /**
     * 获取影院配置信息
     *
     * @param cid
     */
    public static YingCinemaConfigVO cinemaConfig(String cid) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("cid", cid);
        return JSONObject.parseObject(RequestUtil.baseYingRequest("/cinema/config", map), YingCinemaConfigVO.class);
    }

    /**
     * 获取影厅座位图
     */
    public static List<YingCinemaHallSeatVO> queryCinemaHallSeats(String cid, String hallId) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("cid", cid);
        map.put("hall_id", hallId);
        return JSONArray.parseArray(RequestUtil.baseYingRequest("/cinema/hall-seats", map), YingCinemaHallSeatVO.class);
    }

    /**
     * 获取某场次座位状态
     */
    public static List<YingPlaySeatStatusVO> queryPlaySeatStatus(String cid, String playId, String playUpdateTime) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("cid", cid);
        map.put("play_id", playId);
        map.put("play_update_time", playUpdateTime);
        return JSONArray.parseArray(RequestUtil.baseYingRequest("/play/seat-status", map), YingPlaySeatStatusVO.class);
    }

    /**
     * 查看订单处理状态
     */
    public static String orderStatus(String cid, String partnerBuyTicketId) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("cid", cid);
        map.put("partner_order_id", partnerBuyTicketId);
        return RequestUtil.baseYingRequest("/order/status", map);
    }
}
