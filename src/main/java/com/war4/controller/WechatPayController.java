package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.BusinessException;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.enums.CommonErrorResult;
import com.war4.util.OrderUtil;
import com.war4.util.XmlUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dly on 2016/11/1.
 */
@Controller
@RequestMapping(value = "wechatPay")
public class WechatPayController extends BaseController{

    @RequestMapping(value = "payOrder")
    public @ResponseBody
    Response
    payOrder(HttpServletRequest request,String orderId,String clientType) throws Exception{
        return new ObjectResponse<>(payUtilService.createWechatOrder(request.getRemoteAddr(),orderId,clientType));
    }

    @RequestMapping(value = "wechatPayCallBack")
    public void wechatPayCallBack(HttpServletRequest request) throws Exception{
        try {
            String orderId = null;
            String xml = XmlUtil.readXML(request.getInputStream());
            String payResult = XmlUtil.getXMLValueByNode("result_code", xml);
            if (!payResult.equalsIgnoreCase("SUCCESS")) {
                return;
            }
            orderId = OrderUtil.removeOrderSuffix(XmlUtil.getXMLValueByNode("out_trade_no", xml));
            //已支付
            if (baseOrderService.checkOrderPayStatus(orderId)) {
                return;
            }
            payUtilService.payService(orderId);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"fail to callBack");
        }
    }
    @RequestMapping(value = "paySuccess")
    public @ResponseBody
    Response
    paySuccess(String orderId) throws Exception{
        payUtilService.payService(orderId);
        return new Response("支付成功");
    }
}

