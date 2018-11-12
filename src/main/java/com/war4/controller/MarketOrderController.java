package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.MarketOrderExpress;
import com.war4.pojo.MarketOrderGoods;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by Administrator on 2017/2/8.
 */
@Controller
@RequestMapping(value = "/marketorder")
public class MarketOrderController extends BaseController {


    @RequestMapping(value = "/queryAllOrderList", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllOrderList(String payway,String paystatus,String keyword,Integer page, Integer limit) throws Exception {
        CutPage cutPage = marketOrderService.queryAllOrderList(payway,paystatus,keyword,page, limit);
        return new ObjectResponse<>(cutPage);
    }


    @RequestMapping(value = "queryMarketOrderDetail", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMarketOrder(Integer orderId,Integer page,Integer limit) throws Exception {
        CutPage<MarketOrderGoods> marketOrderGoodsCutPage = marketOrderService.queryMarketOrderDetail(orderId, page,limit);
        return new ObjectResponse<>(marketOrderGoodsCutPage);
    }


    @RequestMapping(value = "/createMarketOrderExpress", method = RequestMethod.POST)
    public
    @ResponseBody
    Response createMarketOrderExpress(Integer orderId ,String express, String trackNo, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date sendTime){
         marketOrderService.createMarketOrderExpress(orderId,express,trackNo,sendTime);
         return new ObjectResponse<>("添加发货信息成功");
    }

    @RequestMapping(value = "/updateMarketOrderExpress", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateMarketOrderExpress(Integer orderId ,String express, String trackNo, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date sendTime){
        marketOrderService.updateMarketOrderExpress(orderId,express,trackNo,sendTime);
        return new ObjectResponse<>("修改发货信息成功");
    }

    @RequestMapping(value = "/queryMarketOrderExpress", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMarketOrderExpress(Integer orderId){
        MarketOrderExpress marketOrderExpress = marketOrderService.queryMarketOrderExpress(orderId);
        return new ObjectResponse<>(marketOrderExpress);
    }

}
