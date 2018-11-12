package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.EvaluateDetailed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 评论系统
 * Created by hh on 2017.11.4 0004.
 */
@Controller
@RequestMapping(value = "evaluate")
public class EvaluateController extends BaseController{

    //添加评价
    @RequestMapping(value = "addEvaluate",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addEvaluate(EvaluateDetailed detailed) throws Exception {
        evaluateSystemService.createEvaluateDetailed(detailed);
        return new Response("添加成功");
    }

    //设置订单为评价
    @RequestMapping(value = "setOrderEvaluate",method = RequestMethod.POST)
    public
    @ResponseBody
    Response setOrderEvaluate(String orderId,boolean auto) throws Exception {
        evaluateSystemService.setOrderEvaluate(orderId,auto);
        return new Response("评价成功");
    }

    //查询订单评价
    @RequestMapping(value = "queryOrderEvaluate",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryOrderEvaluate(String orderId) throws Exception {
        return new ObjectResponse<>(evaluateSystemService.queryOrderEvaluateDetailed(orderId));
    }
}
