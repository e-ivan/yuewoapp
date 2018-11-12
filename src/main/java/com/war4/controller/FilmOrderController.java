package com.war4.controller;

import com.war4.base.*;
import com.war4.pojo.FilmOrder;
import com.war4.service.impl.KouDianYingServiceImpl;
import com.war4.util.UserContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/2/8.
 */
@Controller
@RequestMapping(value = "filmorder")
public class FilmOrderController extends BaseController {
    private static final boolean INCLUDE_T_DATA = PropertiesConfig.isCombineFilmData();    //包含天智创客影院数据

    //创建电影订单
    @RequestMapping(value = "createFilmOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response createFilmOrder(Long planId, String cinemaId,String seatNo, String mobile) throws Exception {
        FilmOrder filmOrder;
        if (INCLUDE_T_DATA && !KouDianYingServiceImpl.isKouCinemaId(cinemaId)) {
            filmOrder = filmOrderService.createYingFilmOrder(cinemaId,planId,seatNo,mobile,UserContext.getUserId());
        }else {
            filmOrder = filmOrderService.createFilmOrder(planId, seatNo, mobile, UserContext.getUserId());
        }
        return new ObjectResponse<>(filmOrder);
    }

    @RequestMapping(value = "queryMyOrderList", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyOrderList(Integer status, Integer page, Integer limit) throws Exception {
        CutPage cutPage = filmOrderService.queryMyOrderList(UserContext.getUserId(), status, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    @RequestMapping(value = "queryFilmOrderById", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmOrderById(String orderId) throws Exception {
        FilmOrder filmOrder = filmOrderService.queryFilmOrderById(orderId);
        return new ObjectResponse<>(filmOrder);
    }

    //取消订单
    @RequestMapping(value = "cancelFilmOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response cancelFilmOrder(String orderId, String fkUserId) throws Exception {
        filmOrderService.deleteFilmOrder(orderId, fkUserId);
        return new Response("取消成功！");
    }
    //更新订单手机和优惠券信息
    @RequestMapping(value = "updateFilmOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateFilmOrder(FilmOrder filmOrder) throws Exception {
        return new ObjectResponse<>(filmOrderService.updateFilmOrder(filmOrder));
    }

    //========================天智创客=========================
    //创建电影订单
    @RequestMapping(value = "createYingFilmOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response createYingFilmOrder(String cinemaId,Long planId, String seatNo, String mobile) throws Exception {
        return new ObjectResponse<>(filmOrderService.createYingFilmOrder(cinemaId, planId, seatNo, mobile, UserContext.getUserId()));
    }

}
