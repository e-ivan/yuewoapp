package com.war4.util;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by E_Iva on 2018.1.30.0030.
 */
public class HttpClientUtils {

    private static PoolingHttpClientConnectionManager connectionManager = null;
    private static HttpClientBuilder httpBuilder = null;
    private static RequestConfig requestConfig = null;


    private static final int MAX_CONNECTION = 100;

    private static final int DEFAULT_MAX_CONNECTION = 20;

    private static String IP = "cnivi.com.cn";
    private static int PORT = 80;

    static {
        //设置http的状态参数
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(30000)
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(5000)
                .build();

        HttpHost target = new HttpHost(IP, PORT);
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_CONNECTION);//客户端总并行链接最大数
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTION);//每个主机的最大并行链接数
//        connectionManager.setMaxPerRoute(new HttpRoute(target), 20);
        httpBuilder = HttpClients.custom();
        httpBuilder.setConnectionManager(connectionManager);
    }

    public static CloseableHttpClient getHttpClient() {
        return httpBuilder.build();
    }


    public static HttpUriRequest getRequestMethod(Map<String, String> map, String url, String method) {
        List<NameValuePair> params = parseNameValuePair(map);
        HttpUriRequest reqMethod = null;
        if ("post".equals(method)) {
            reqMethod = RequestBuilder.post().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                    .setConfig(requestConfig).build();
        } else if ("get".equals(method)) {
            reqMethod = RequestBuilder.get().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                    .setConfig(requestConfig).build();
        }
        return reqMethod;
    }

    public static List<NameValuePair> parseNameValuePair(Map<String, String> map) {
        List<NameValuePair> params = new ArrayList<>();
        map.forEach((n, v) -> params.add(new BasicNameValuePair(n, v)));
        return params;
    }

    public static void main(String args[]) throws IOException {

    }
}
