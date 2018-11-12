package com.war4.base;

import com.war4.util.DateUtil;
import com.war4.util.MD5Util;
import com.war4.util.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

/**
 * Created by dly on 2016/8/10.
 */
public class PropertiesConfig {
    private static Properties properties;
    static {
        System.out.println("配置文件读取");
        try{
            properties=new Properties();
            InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("common.properties");
            properties.load(new InputStreamReader(inputStream, "UTF-8"));
            PropertyConfigurator.configure(properties);
        }catch (Exception e){
            System.out.println("配置文件读取错误");
        }
    }

    public static String getFileRoot() {
        return properties.getProperty("fileRoot");
    }

    public static String getFileLogicRoot(){
        return properties.getProperty("fileLogicRoot");
    }
    public static String getSuperManagerAccount(){
        return properties.getProperty("superManagerAccount");
    }

    public static String getSuperManagerNickName(){
        return properties.getProperty("superManagerNickName");
    }

    public static String getSuperManagerPassword(){
        return properties.getProperty("superManagerPassword");
    }
    public static String getAlipayNotifyUrl(){
        return properties.getProperty("alipayNotifyUrl");
    }
    public static String getDefaultUserPhotoHead(){
        return properties.getProperty("defaultUserPhotoHead");
    }
    public static String getWechatNotifyUrl(){
        return properties.getProperty("wechatNotifyUrl");
    }

    public static String getJpushAppkey(){
        return properties.getProperty("JpushAppkey");
    }
    public static String getRongYunApi(){
        return properties.getProperty("rongYunApi");
    }
    public static String getRongYunAppKey(){
        return properties.getProperty("rongYunAppKey");
    }
    public static String getRongYunAppSecret(){
        return properties.getProperty("rongYunAppSecret");
    }
    public static String getRongYunLuckyMoneyAppSecret(){
        return properties.getProperty("rongYunLuckyMoneyAppSecret");
    }
    public static String getAmapAppKey(){
        return properties.getProperty("amapAppKey");
    }
    public static String getAmapAppSecret(){
        return properties.getProperty("amapAppSecret");
    }
    public static String getAmapRequestApi(){
        return properties.getProperty("amapRequestApi");
    }
    public static String getJpushAppMasterSecret(){
        return properties.getProperty("JpushAppMasterSecret");
    }
    public static String getKouDianYingKey(){
        return properties.getProperty("kouDianYingKey");
    }
    public static String getKouDianYingData(){
        return properties.getProperty("kouDianYingData");
    }
    public static String getKouDianYingOrder(){
        return properties.getProperty("kouDianYingOrder");
    }
    public static boolean isCombineFilmData(){
        String b = properties.getProperty("combineFilmData");
        return Boolean.parseBoolean(b);
    }
    public static boolean isImgFileCompress(){
        String b = properties.getProperty("imgFileCompress");
        return Boolean.parseBoolean(b);
    }
    public static boolean isProduction(){
        String b = properties.getProperty("production");
        return Boolean.parseBoolean(b);
    }
    public static String getSystemSendSmsPhone(){
        String systemSendSmsPhone = properties.getProperty("systemSendSmsPhone");
        return StringUtils.isNotBlank(systemSendSmsPhone) ? systemSendSmsPhone : "";
    }
    public static String getMovieAdminPhone(){
        String movieAdminPhone = properties.getProperty("movieAdminPhone");
        return StringUtils.isNotBlank(movieAdminPhone) ? movieAdminPhone : "";
    }
    public static Date getRecommendStartDate(){
        String recommendStartDate = properties.getProperty("recommendStartDate");
        if (StringUtils.isNotBlank(recommendStartDate)){
            return DateUtil.parseDate(recommendStartDate,"yyyy-MM-dd");
        }
        return null;
    }
    public static Date getRecommendEndDate(){
        String recommendStartDate = properties.getProperty("recommendEndDate");
        if (StringUtils.isNotBlank(recommendStartDate)){
            return DateUtil.endOfDay(DateUtil.parseDate(recommendStartDate,"yyyy-MM-dd"));
        }
        return null;
    }
    public static Date getUserRegisterCouponStartDate(){
        String userRegisterCouponStartDate = properties.getProperty("userRegisterCouponStartDate");
        if (StringUtils.isNotBlank(userRegisterCouponStartDate)){
            return DateUtil.parseDate(userRegisterCouponStartDate,"yyyy-MM-dd");
        }
        return null;
    }
    public static Date getUserRegisterCouponEndDate(){
        String userRegisterCouponEndDate = properties.getProperty("userRegisterCouponEndDate");
        if (StringUtils.isNotBlank(userRegisterCouponEndDate)){
            return DateUtil.endOfDay(DateUtil.parseDate(userRegisterCouponEndDate,"yyyy-MM-dd"));
        }
        return null;
    }
    public static Integer getRecommendNum(){
        return Integer.valueOf(properties.getProperty("recommendNum"));
    }
    public static String getRecommendCouponId(){
        return properties.getProperty("recommendCouponId");
    }
    public static String getBeRecommendedCouponId(){
        return properties.getProperty("beRecommendedCouponId");
    }
    public static String getUserRegisterCouponId(){
        return properties.getProperty("userRegisterCouponId");
    }
    public static String getKouDianYingChannelId(){
        return properties.getProperty("kouDianYingChannelId");
    }
    public static String getYinghezhongAuthcode(){
        return properties.getProperty("yinghezhongAuthcode");
    }
    public static String getYinghezhongPid(){
        return properties.getProperty("yinghezhongPid");
    }
    public static String getYinghezhongApi(){
        return properties.getProperty("yinghezhongApi");
    }
    public static int getDefAmountDay(){
        try {
            return Integer.parseInt(properties.getProperty("defAmountDay"));
        }catch (Exception e){
            return 3;
        }
    }
    public static String getAppDownloadPath(){
        return properties.getProperty("appDownloadPath");
    }
    public static String getIosAppVersion(){
        return properties.getProperty("iosAppVersion");
    }
    public static String getShopUrl(){
        return properties.getProperty("shopUrl");
    }

    public static Date getDatingGetFilmsStartDate(){
        String datingGetFilmsStartDate = properties.getProperty("datingGetFilmsStartDate");
        if (StringUtils.isNotBlank(datingGetFilmsStartDate)){
            return DateUtil.parseDate(datingGetFilmsStartDate,"yyyy-MM-dd");
        }
        return null;
    }
    public static Date getDatingGetFilmsEndDate(){
        String datingGetFilmsEndDate = properties.getProperty("datingGetFilmsEndDate");
        if (StringUtils.isNotBlank(datingGetFilmsEndDate)){
            return DateUtil.endOfDay(DateUtil.parseDate(datingGetFilmsEndDate,"yyyy-MM-dd"));
        }
        return null;
    }

    public  static  String getDatingGetFilmsCouponId(){
        return properties.getProperty("datingGetFilmsCouponId");
    }
    public  static  String getFilmOrderMarketCouponName(){
        return properties.getProperty("filmOrderMarketCouponName");
    }
    public  static  int getMarketCouponNameValid(){
        try {
            String marketCouponNameValid = properties.getProperty("marketCouponNameValid");
            return Integer.parseInt(marketCouponNameValid);
        }catch (Exception e){
            return Integer.MAX_VALUE;
        }
    }
    public static String getRemoteSecret(){
        return MD5Util.encode(MD5Util.encode(properties.getProperty("remoteUsername")) + properties.getProperty("remotePassword"));
    }

    public static long getMaxInternalUserDistance(){
        try {
            return Long.parseLong(properties.getProperty("maxInternalUserDistance"));
        }catch (Exception e){
            return 0;
        }
    }
    public static long getMinInternalUserDistance(){
        try {
            return Long.parseLong(properties.getProperty("minInternalUserDistance"));
        }catch (Exception e){
            return 0;
        }
    }
    public static int getMaxInternalUserNum(){
        try {
            return Integer.parseInt(properties.getProperty("maxInternalUserNum"));
        }catch (Exception e){
            return 0;
        }
    }
    public static int getMinInternalUserNum(){
        try {
            return Integer.parseInt(properties.getProperty("minInternalUserNum"));
        }catch (Exception e){
            return 0;
        }
    }

    public static boolean isOnlyOpposite(){
        return Boolean.parseBoolean(properties.getProperty("onlyOpposite"));
    }

    public static int getUserJoinCount(){
        try {
            return Integer.parseInt(properties.getProperty("userJoinCount"));
        }catch (Exception e){
            return 0;
        }
    }

    public static Integer getAuctionLowestPeriod(){
        try {
            return Integer.parseInt(properties.getProperty("auctionLowestPeriod"));
        }catch (Exception e){
            return 0;
        }
    }
    public static Integer getIntervalAuctionFinishWithFilmStartTime(){
        try {
            return Integer.parseInt(properties.getProperty("intervalAuctionFinishWithFilmStartTime"));
        }catch (Exception e){
            return 0;
        }
    }
    public static BigDecimal getAuctionDepositProportion(){
        try {
            return new BigDecimal(properties.getProperty("auctionDepositProportion"));
        }catch (Exception e){
            return BigDecimal.valueOf(0);
        }
    }
    public static BigDecimal[] getAuctionRewardProportion(){
        String auctionRewardProportion = properties.getProperty("auctionRewardProportion");
        String[] split = auctionRewardProportion.split("/");
        BigDecimal[] auctionRewards = new BigDecimal[split.length];
        try {
            for (int i = 0; i < auctionRewards.length; i++) {
                auctionRewards[i] = new BigDecimal(split[i]);
            }
            return auctionRewards;
        }catch (Exception e){
            return auctionRewards;
        }
    }

    public static int getAuctionOrderDefaultEvaluateDay(){
        try {
            return Integer.parseInt(properties.getProperty("auctionOrderDefaultEvaluateDay"));
        }catch (Exception e){
            return 30;
        }
    }
    public static BigDecimal getVideoChatCostStartStep(){
        try {
            return new BigDecimal(properties.getProperty("videoChatCostStartStep"));
        }catch (Exception e){
            return BigDecimal.valueOf(0);
        }
    }

    public static BigDecimal getVideoChatCostOneStep(){
        try {
            return new BigDecimal(properties.getProperty("videoChatCostOneStep"));
        }catch (Exception e){
            return BigDecimal.valueOf(0);
        }
    }

    public static BigDecimal[] getVideoChatCostProportion(){
        String videoChatCostProportion = properties.getProperty("videoChatCostProportion");
        String[] split = videoChatCostProportion.split("/");
        BigDecimal[] videoChatCosts = new BigDecimal[split.length];
        try {
            for (int i = 0; i < videoChatCosts.length; i++) {
                videoChatCosts[i] = new BigDecimal(split[i]);
            }
            return videoChatCosts;
        }catch (Exception e){
            return videoChatCosts;
        }
    }

    public static Integer getVideoChatCostStartDuration(){
        try {
            return new Integer(properties.getProperty("videoChatCostStartDuration"));
        }catch (Exception e){
            return 0;
        }
    }

    public static Integer getVideoChatCostTimeLeftTips(){
        try {
            return new Integer(properties.getProperty("videoChatCostTimeLeftTips"));
        }catch (Exception e){
            return 0;
        }
    }

    public static String getVoteActivityId(){
        return properties.getProperty("voteActivityId");
    }

}
