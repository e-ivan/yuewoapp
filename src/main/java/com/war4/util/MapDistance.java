package com.war4.util;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.6.12 0012.
 */
public class MapDistance {
    private final static double PI = Math.PI; // 圆周率
    private final static double R = 6378137; // 地球的半径


    /**
     * 根据两个位置的经纬度，来计算两地的距离（单位为m）
     * 参数为String类型
     * @param lng1Str 用户经度
     * @param lat1Str 用户纬度
     * @param lng2Str 商家经度
     * @param lat2Str 商家纬度
     * @return
     */
    public static Double getDistance(String lng1Str, String lat1Str, String lng2Str, String lat2Str) {
        Double lng1 = !NumberUtil.isDecimals(lat1Str) ? 0 : Double.parseDouble(lng1Str) ;
        Double lat1 = !NumberUtil.isDecimals(lat1Str) ? 0 :Double.parseDouble(lat1Str);
        Double lng2 = !NumberUtil.isDecimals(lat1Str) ? 0 :Double.parseDouble(lng2Str);
        Double lat2 = !NumberUtil.isDecimals(lat1Str) ? 0 :Double.parseDouble(lat2Str);

        double x, y, distance;
        x = (lng2 - lng1) * PI * R
                * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
        y = (lat2 - lat1) * PI * R / 180;
        distance = Math.hypot(x, y) ;
        return new BigDecimal(distance).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取当前用户一定距离以内的经纬度值
     * 单位米 return minLat
     * 最小经度 minLng
     * 最小纬度 maxLat
     * 最大经度 maxLng
     * 最大纬度 minLat
     */
    public static Map getAround(String latStr, String lngStr, String raidus) {
        Map<String,String> map = new HashMap<>();

        Double latitude = Double.parseDouble(latStr);// 传值给纬度
        Double longitude = Double.parseDouble(lngStr);// 传值给经度

        Double degree = (24901 * 1609) / 360.0; // 获取每度
        double raidusMile = Double.parseDouble(raidus);

        Double mpdLng = Double.parseDouble((degree * Math.cos(latitude * (Math.PI / 180))+"").replace("-", ""));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        //获取最小经度
        Double minLng = longitude - radiusLng;
        // 获取最大经度
        Double maxLng = longitude + radiusLng;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        // 获取最小纬度
        Double minLat = latitude - radiusLat;
        // 获取最大纬度
        Double maxLat = latitude + radiusLat;

        map.put("minLat", minLat+"");
        map.put("maxLat", maxLat+"");
        map.put("minLng", minLng+"");
        map.put("maxLng", maxLng+"");

        return map;
    }

    /**
     * 根据经纬度获取地址信息
     * @param lng
     * @param lat
     * @return
     */
    public static String getAdd(String lng, String lat) {
        //lat 小  lng  大
        //参数解释: 纬度,经度 type 001 (100代表道路，010代表POI，001代表门址，111可以同时显示前三项)
        String urlString = "http://gc.ditu.aliyun.com/regeocoding?l="+lat+","+lng+"&type=010";
        String res = "";
        try {
            URL url = new URL(urlString);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                res += line+"\n";
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error in wapaction,and e is " + e.getMessage());
        }
        return res;
    }

    /**
     * 根据经纬度获取城市名称
     * @param lng   经度
     * @param lat   纬度
     * @return
     */
    public static String getCityName(String lng,String lat){
        String admName;
        try {
            admName = JSON.parseObject(getAdd(lng, lat)).getJSONArray("addrList").getJSONObject(0).getString("admName");
        }catch (Exception e){
            return "广州";
        }
        String[] split = admName.split(",");
        return split[1].replace("市", "");
    }

    public static void main(String[] args) {
        System.out.println(getCityName("113.334113", "23.149542"));
    }

}
