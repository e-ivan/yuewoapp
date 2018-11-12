package com.war4.util;

import com.alibaba.fastjson.JSONObject;
import com.war4.base.BusinessException;
import com.war4.base.PropertiesConfig;
import com.war4.enums.CommonErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * 请求处理工具
 * Created by hh on 2017.7.22 0022.
 */
@Slf4j
public class RequestUtil {
    private static final String YINGHEZHONG_API = PropertiesConfig.getYinghezhongApi();
    private static final String AUTHCODE = PropertiesConfig.getYinghezhongAuthcode();
    private static final String PID = PropertiesConfig.getYinghezhongPid();

    private static final String APP_KEY = "App-Key";
    private static final String NONCE = "Nonce";
    private static final String TIMESTAMP = "Timestamp";
    private static final String SIGNATURE = "Signature";
    private static final String RONG_YUN_API = PropertiesConfig.getRongYunApi();
    private static final String RONG_YUN_APP_KEY = PropertiesConfig.getRongYunAppKey();
    private static final String RONG_YUN_APP_SECRET = PropertiesConfig.getRongYunAppSecret();

    private static final String AMAP_APP_KEY = PropertiesConfig.getAmapAppKey();
    private static final String AMAP_APP_SECRET = PropertiesConfig.getAmapAppSecret();
    private static final String AMAP_REQUEST_API = PropertiesConfig.getAmapRequestApi();

    private static void kouBuildEnc(TreeMap<String, String> map) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        map.put("time_stamp", timestamp);
        StringBuilder value = new StringBuilder(100);
        map.forEach((k, v) -> {
            if (!StringUtils.equals(k, "enc")) {
                value.append(v);
            }
        });
        String enc = null;
        try {
            enc = MD5Util.encode(URLEncoder.encode(value.toString() + PropertiesConfig.getKouDianYingKey(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put("enc", StringUtils.lowerCase(enc));
    }

    public static JSONObject baseKouDianYingRequest(TreeMap<String, String> paramMap, String url) {
        kouBuildEnc(paramMap);//先签名
        HttpUriRequest post = HttpClientUtils.getRequestMethod(paramMap, url, "get");
        post.addHeader("channel_id", PropertiesConfig.getKouDianYingChannelId());
        CloseableHttpClient client = HttpClientUtils.getHttpClient();
        try (CloseableHttpResponse response = client.execute(post)) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.getInteger("status").equals(0)) {
                return jsonObject;
            }
            log.warn("扣电影接口调用失败 ：" + jsonObject);
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, jsonObject.getString("error"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络繁忙");
        }
    }

    private static String aMapBuildSig(Map<String, String> map, String secret) {
        StringBuilder strBuild = new StringBuilder();
        List<Map.Entry<String, String>> infoIds = new ArrayList<>(map.entrySet());
        //排序
        infoIds.sort((map1, map2) -> {
            if (StringUtils.equals(map1.getKey(), map2.getKey())) {
                return StringUtils.isAnyEmpty(map1.getValue(),map2.getValue()) ? 0 : (map1.getValue()).compareTo(map2.getValue());
            } else {
                return (map1.getKey()).compareTo(map2.getKey());
            }
        });
        for (int i = 0; i < infoIds.size(); i++) {
            Map.Entry<String, String> entry = infoIds.get(i);
            String value = entry.getValue();
            if (StringUtils.isNoneEmpty(value) && !StringUtils.equals("sig", entry.getKey())) {
                strBuild.append(entry.getKey()).append("=").append(value);
            }
            if (i != infoIds.size() - 1) {
                strBuild.append("&");
            }
        }
        return strBuild.append(secret).toString();
    }

    public static JSONObject baseAmapRequest(Map<String, String> paramMap, String action) {
        paramMap.put("key", AMAP_APP_KEY);
        //加密签名
        paramMap.put("sig", aMapBuildSig(paramMap,AMAP_APP_SECRET));
        HttpUriRequest post = HttpClientUtils.getRequestMethod(paramMap, jointUrl(AMAP_REQUEST_API, action), "get");
        CloseableHttpClient client = HttpClientUtils.getHttpClient();
        try (CloseableHttpResponse response = client.execute(post)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (!jsonObject.getBoolean("status")) {
                    throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络繁忙");
                }
                return jsonObject;
            } else {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络繁忙");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络繁忙");
        }
    }

    private static String buildYingSignParam(Map<String, String> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(key).append("=").append(value);
            if (i != keys.size() - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public static String baseYingRequest(String action, TreeMap<String, String> paramMap) {
        paramMap.put("format", "json");
        paramMap.put("pid", PID);
        String sig = MD5Util.encode(
                MD5Util.encode(
                        AUTHCODE
                                + buildYingSignParam(paramMap))
                        + AUTHCODE);//加密签名
        paramMap.put("_sig", sig);
        HttpUriRequest post = HttpClientUtils.getRequestMethod(paramMap, jointUrl(YINGHEZHONG_API, action), "post");
        CloseableHttpClient client = HttpClientUtils.getHttpClient();
        try (CloseableHttpResponse response = client.execute(post)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                JSONObject res = JSONObject.parseObject(EntityUtils.toString(response.getEntity())).getJSONObject("res");
                if (res.getBooleanValue("status")) {
                    return res.getString("data");
                }
                log.warn("鼎新调用接口失败 ：" + res);
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, res.getString("errorMessage"));
            } else {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络繁忙");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络繁忙");
        }
    }

    public static JSONObject baseRongYunRequest(Map<String, String> paramMap, String action) {
        String nonce = String.valueOf(Math.random() * 1000000);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        HttpUriRequest post = HttpClientUtils.getRequestMethod(paramMap, jointUrl(RONG_YUN_API, action) + ".json", "post");
        post.addHeader(APP_KEY, RONG_YUN_APP_KEY);
        post.addHeader(NONCE, nonce);
        post.addHeader(TIMESTAMP, timestamp);
        post.addHeader(SIGNATURE, CodeUtil.hexSHA1(RONG_YUN_APP_SECRET + nonce + timestamp));
        CloseableHttpClient client = HttpClientUtils.getHttpClient();
        try (CloseableHttpResponse response = client.execute(post)) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer code = jsonObject.getInteger("code");
            if (code == null || code != 200) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络繁忙");
            }
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "网络繁忙");
        }
    }

    /**
     * 拼接url
     *
     * @param api
     * @param action
     */
    public static String jointUrl(String api, String action) {
        if (api == null || action == null) {
            return "";
        }
        String apiTrim = api.trim();
        String actionTrim = action.trim();
        Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://|[fF][tT][pP]://).*");
        if (!pattern.matcher(apiTrim).matches()) {
            apiTrim = "http://" + apiTrim;
        }
        boolean apiEnd = apiTrim.endsWith("/");
        boolean actionEnd = actionTrim.startsWith("/");
        String url;
        if (apiEnd) {//如果api存在斜杆
            if (actionEnd) {//如果action也存在斜杆
                //去掉action中的斜杆
                url = apiTrim + actionTrim.substring(1);
            } else {
                url = apiTrim + actionTrim;
            }
        } else {
            if (actionEnd) {//如果action存在斜杆
                //去掉action中的斜杆
                url = apiTrim + actionTrim;
            } else {
                url = apiTrim + "/" + actionTrim;
            }
        }
        return url;
    }
}
