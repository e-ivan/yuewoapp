package com.war4.service;

import com.war4.vo.film.FilmOrderVO;

import java.util.List;
import java.util.Timer;

/**
 * 抠电影订单服务
 * Created by hh on 2017.6.14 0014.
 */
public interface KouDianYingOrderService {

    /**
     * 查询电影订单
     * @param orderId
     * @param timer
     */
    List<FilmOrderVO> queryMovieOrderById(String orderId, Timer timer) throws Exception;

    /**
     * 确认订单
     * @param orderId
     */
    void confirmMovieOrder(String orderId) throws Exception;
    /**
     * 创建订单
     * @param planId        排期编号
     * @param seatNo        座位id
     * @param sendMessage   是否发送信息
     * @param mobile        手机号码
     */
    FilmOrderVO addOrder(String planId, String seatNo, boolean sendMessage, String mobile) throws Exception;
    //删除订单
    void deleteOrder(String orderId) throws Exception;
    //查询订单状态
    FilmOrderVO queryOrder(String orderId) throws Exception;
}
