package com.war4.util;

import com.alibaba.druid.util.StringUtils;
import com.war4.base.BusinessException;
import com.war4.base.PropertiesConfig;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.WechatParams;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

/**
 * 微信支付
 * Created by hh on 2017.9.21 0021.
 */
public class WechatPayUtil {

    private static final String NOTIFY_URL = PropertiesConfig.getWechatNotifyUrl();
    private static final String NONCE_STR = "vhawruiyfwnjkfhrs";

    /**
     * 创建微信支付参数
     */
    public static Map<String, String> createPayParams(String ip, String clientType, BigDecimal totalFee, String outTradeNo, String title) {
        WechatParams wechatParams = WechatParams.getWechatParamsByClientType(clientType);
        if (wechatParams == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "支付类型不存在");
        }
//        String openId = getOpenIdByCode(code);
        String payMoney = NumberUtil.fromYuanToFen(totalFee);
        String tradeType = "APP";
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put("appid", wechatParams.getAppId());
        sortedMap.put("mch_id", wechatParams.getMerchantId());
        sortedMap.put("nonce_str", NONCE_STR);
        sortedMap.put("body", title);
        sortedMap.put("out_trade_no", outTradeNo);
        sortedMap.put("total_fee", payMoney);
        sortedMap.put("spbill_create_ip", ip);
        sortedMap.put("notify_url", NOTIFY_URL);
        sortedMap.put("trade_type", tradeType);

        String sign = createSign(sortedMap, wechatParams.getApiKey());

        String xml = "<xml>" +
                "<appid>" + wechatParams.getAppId() + "</appid>" +
                "<mch_id>" + wechatParams.getMerchantId() + "</mch_id>" +
                "<nonce_str>" + NONCE_STR + "</nonce_str>" +
                "<sign>" + sign + "</sign>" +
                "<body>" + title + "</body>" +
                "<out_trade_no>" + outTradeNo + "</out_trade_no>" +
                "<total_fee>" + payMoney + "</total_fee>" +
                "<spbill_create_ip>" + ip + "</spbill_create_ip>" +
                "<notify_url>" + NOTIFY_URL + "</notify_url>" +
                "<trade_type>" + tradeType + "</trade_type>" +
                "</xml>";

        String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        String prepayId = getPrepayId(createOrderURL, xml);
        if (StringUtils.isEmpty(prepayId)) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "调用微信统一下单接口失败");
        }

        SortedMap<String, String> finalpackage = new TreeMap<String, String>();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String packages = "Sign=WXPay";
        finalpackage.put("appid", wechatParams.getAppId());
        finalpackage.put("noncestr", NONCE_STR);
        finalpackage.put("timestamp", timestamp);
        finalpackage.put("partnerid", wechatParams.getMerchantId());
        finalpackage.put("prepayid", prepayId);
        finalpackage.put("package", packages);
        String finalsign = createSign(finalpackage, wechatParams.getApiKey());

        HashMap<String, String> resultMap = new HashMap();
        resultMap.put("appid", wechatParams.getAppId());
        resultMap.put("timeStamp", timestamp);
        resultMap.put("nonceStr", NONCE_STR);
        resultMap.put("package", packages);
        resultMap.put("sign", finalsign);
        resultMap.put("prepayId", prepayId);
        resultMap.put("partnerId", wechatParams.getMerchantId());
        return resultMap;
    }

    public static Map<String, String> test(String outRefundNo, String clientType, BigDecimal totalFee, String outTradeNo) {
        WechatParams wechatParams = WechatParams.getWechatParamsByClientType(clientType);
        if (wechatParams == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "支付类型不存在");
        }
        String payMoney = NumberUtil.fromYuanToFen(totalFee);
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put("appid", wechatParams.getAppId());
        sortedMap.put("mch_id", wechatParams.getMerchantId());
        sortedMap.put("nonce_str", NONCE_STR);
        sortedMap.put("out_trade_no", outTradeNo);
        sortedMap.put("out_refund_no", outRefundNo);
        sortedMap.put("total_fee", payMoney);
        sortedMap.put("refund_fee", payMoney);
        sortedMap.put("refund_desc", "有钱任性");

        String sign = createSign(sortedMap, wechatParams.getApiKey());

        String xml = "<xml>" +
                "<appid>" + wechatParams.getAppId() + "</appid>" +
                "<mch_id>" + wechatParams.getMerchantId() + "</mch_id>" +
                "<nonce_str>" + NONCE_STR + "</nonce_str>" +
                "<sign>" + sign + "</sign>" +
                "<out_refund_no>" + outRefundNo + "</out_refund_no>" +
                "<out_trade_no>" + outTradeNo + "</out_trade_no>" +
                "<total_fee>" + payMoney + "</total_fee>" +
                "<refund_fee>" + payMoney + "</refund_fee>" +
                "<refund_desc>有钱任性</refund_desc>" +
                "</xml>";

        String createOrderURL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
        Map v_map = new HashMap();
        int result;
        String respost = null;
        String prepay_id = "";
        HttpClient client = new HttpClient();
//        //设置超时时间
        client.getHttpConnectionManager().getParams()
                .setConnectionTimeout(5000);
        client.getParams().setParameter("http.socket.timeout", new Integer(60000));

        PostMethod post = new PostMethod(createOrderURL);
        try {
            //设置参数编码
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            post.setRequestBody(xml);//这里添加xml字符串
            client.executeMethod(post);
            //返回的结果集
            respost = IOUtils.toString(new InputStreamReader(post.getResponseBodyAsStream()));
            v_map = XmlUtil.doXMLParse(respost);
        }catch (Exception e){
            e.printStackTrace();
        }
        return v_map;
    }

    public static Map<String, String> query(String clientType, String outTradeNo) {
        WechatParams wechatParams = WechatParams.getWechatParamsByClientType(clientType);
        if (wechatParams == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "支付类型不存在");
        }
        SortedMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.put("appid", wechatParams.getAppId());
        sortedMap.put("mch_id", wechatParams.getMerchantId());
        sortedMap.put("nonce_str", NONCE_STR);
        sortedMap.put("out_trade_no", outTradeNo);

        String sign = createSign(sortedMap, wechatParams.getApiKey());

        String xml = "<xml>" +
                "<appid>" + wechatParams.getAppId() + "</appid>" +
                "<mch_id>" + wechatParams.getMerchantId() + "</mch_id>" +
                "<nonce_str>" + NONCE_STR + "</nonce_str>" +
                "<sign>" + sign + "</sign>" +
                "<out_trade_no>" + outTradeNo + "</out_trade_no>" +
                "</xml>";

        String createOrderURL = "https://api.mch.weixin.qq.com/pay/orderquery";
        Map v_map = new HashMap();
        int result;
        String respost = null;
        HttpClient client = new HttpClient();
//        //设置超时时间
        client.getHttpConnectionManager().getParams()
                .setConnectionTimeout(5000);
        client.getParams().setParameter("http.socket.timeout", new Integer(60000));

        PostMethod post = new PostMethod(createOrderURL);
        try {
            //设置参数编码
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            post.setRequestBody(xml);//这里添加xml字符串
            client.executeMethod(post);
            //返回的结果集
            respost = IOUtils.toString(new InputStreamReader(post.getResponseBodyAsStream()));
            v_map = XmlUtil.doXMLParse(respost);
        }catch (Exception e){
            e.printStackTrace();
        }
        return v_map;
    }

    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     */
    private static String createSign(SortedMap<String, String> packageParams, String apiKey) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"Sign".equals(k)
                    && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + apiKey);
        String sign = MD5Util.encode(sb.toString()).toUpperCase();
        return sign;
    }

    /**
     * 获取
     *
     * @param url
     * @param xmlParam
     * @return
     */
    private static String getPrepayId(String url, String xmlParam) {
        Map v_map = new HashMap();
        int result;
        String respost = null;
        String prepay_id = "";
        HttpClient client = new HttpClient();
//        //设置超时时间
        client.getHttpConnectionManager().getParams()
                .setConnectionTimeout(5000);
        client.getParams().setParameter("http.socket.timeout", new Integer(60000));

        PostMethod post = new PostMethod(url);
        try {
            //设置参数编码
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            post.setRequestBody(xmlParam);//这里添加xml字符串
            client.executeMethod(post);
            //返回的结果集
            respost = IOUtils.toString(new InputStreamReader(post.getResponseBodyAsStream()));
            v_map = XmlUtil.doXMLParse(respost);
            prepay_id = (String) v_map.get("prepay_id");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放链接
            post.releaseConnection();
            ((SimpleHttpConnectionManager) client.getHttpConnectionManager()).shutdown();
        }
        return prepay_id;
    }
}
