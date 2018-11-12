package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.util.OrderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by dly on 2016/10/20.
 */
@Controller
@RequestMapping(value = "/alipay")
public class AlipayController extends BaseController {

    public Logger logger = Logger.getLogger(AlipayController.class.getName());

    /**
     * 创建并获取支付宝订单详情
     *
     * @return 订单详情
     */
    @RequestMapping(value = "createOrderInfo")
    @ResponseBody
    public Response createOrderInfo(String orderId) throws Exception {
        String orderInfo = payUtilService.createAlipayOrder(orderId);
        return new ObjectResponse<>(orderInfo);
    }

    /**
     * 服务器异步通知页面路径
     */
    @RequestMapping(value = "notifyUrl")
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("进来了！");
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        //商户订单号
        String out_trade_no = params.get("out_trade_no");
        String orderId = OrderUtil.removeOrderSuffix(out_trade_no);
        //已支付
        if (baseOrderService.checkOrderPayStatus(orderId,response,"failure")) {
            return;
        }

        //支付宝交易号
        String trade_no = params.get("trade_no");
        //交易状态
        String trade_status = params.get("trade_status");
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)
//        boolean result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, "utf-8");
//        if (result) {
        //请在这里加上商户的业务逻辑程序代码
        //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
        if (trade_status.equals("TRADE_FINISHED")) {
            System.out.println("TRADE_FINISHED");
            payUtilService.payService(orderId);
            response.getWriter().print("success");
            return;
        } else if (trade_status.equals("TRADE_SUCCESS")) {
            System.out.println("TRADE_SUCCESS");
            payUtilService.payService(orderId);
            response.getWriter().print("success");

        }
//            else {//验证失败
//            response.getWriter().print("failure");
//        }

    }

}

