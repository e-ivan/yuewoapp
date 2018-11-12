package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.FilmOrder;

/**
 * Created by Administrator on 2017/1/12.
 */
public interface FilmOrderService {
    void setOrderPaySuccess(String orderId) throws Exception;
    FilmOrder queryFilmOrderById(String orderId) throws Exception;
    CutPage<FilmOrder> queryMyOrderList(String fkUserId,Integer status,Integer page,Integer limit) throws Exception;
    CutPage<FilmOrder> queryAllOrderList(String src,Integer payType,String status,String keyword,Integer page, Integer limit);
    void disposeOverdueOrder(Integer minute);

    /**
     * 创建电影票订单
     * @param planId    排期编号
     * @param seatNo    座位id
     * @param mobile    电话
     * @param userId  购买人id
     * @return          返回订单号
     */
    FilmOrder createFilmOrder(Long planId, String seatNo, String mobile, String userId) throws Exception;
    //更新影票订单信息
    FilmOrder updateFilmOrder(FilmOrder filmOrder) throws Exception;

    //删除用户电影订单
    void deleteFilmOrder(String orderId, String fkUserId) throws Exception;
    //取消用户电影订单（用于超时未付款）
    void cancelFilmOrder(String orderId) throws Exception;

    //===================天智创客====================
    /**
     * 创建电影票订单
     * @param planId    排期编号
     * @param seatNo    座位id
     * @param mobile    电话
     * @param userId  购买人id
     * @return          返回订单号
     */
    FilmOrder createYingFilmOrder(String cinemaId,Long planId, String seatNo, String mobile, String userId) throws Exception;
}
