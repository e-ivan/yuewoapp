package com.war4.util;

import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dly on 2016/7/29.
 */
public class DateUtil {
    private DateUtil() {
    }

    public static final String MINUTE = "minute";
    public static final String HOUR = "hour";
    public static final String DAY = "day";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    private static DateFormat yyMMddHHmmss = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
    /**
     * 得到一天的最后一秒钟
     *
     * @param d
     * @return
     */
    public static Date endOfDay(Date d) {
        return DateUtils.addSeconds(DateUtils.addDays(DateUtils.truncate(d, Calendar.DATE), 1), -1);
    }
    public static Date parseDate(String date,String pattern){
        SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getInstance();
        df.applyPattern(pattern);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Timestamp convert(String timeStr) {
        if (timeStr == null || timeStr.equals("")) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        Timestamp ts = null;
        try {
            ts = new Timestamp(format.parse(timeStr).getTime());
            System.out.println(ts.toString());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ts;
    }

    public static Timestamp getNowDate() {
        Date nowDate = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(nowDate);
        return DateUtil.convert(dateStr);
    }


    /**
     * 取两个时间相差的分钟数，不够一分钟取一分钟(延迟5秒)
     */
    public static Long getMinuteUp(Date firstTime, Date endTime) {
        long second = getDelaySecond(firstTime,endTime);
        return BigDecimal.valueOf(second).divide(new BigDecimal("60"), RoundingMode.UP).longValue();
    }

    /**
     * 取两个时间相差的分钟数，够一分钟才取一分钟（延时5秒）
     */
    public static Long getMinuteDown(Date firstTime, Date endTime) {
        long second = getDelaySecond(firstTime,endTime);
        return BigDecimal.valueOf(second).divide(new BigDecimal("60"), RoundingMode.DOWN).longValue();
    }

    private static Long getDelaySecond(Date firstTime, Date endTime){
        long second = Math.abs((firstTime.getTime() - endTime.getTime()) / 1000);
        long cur = second % 60;
        if (cur >= 55 && cur <= 60 || cur >= 0 && cur <= 5) {
            if (cur >= 55) {
                second += 60 - cur;
            }else {
                second -= cur;
            }
        }
        return ++second;
    }
    //获取时间间隔
    public static Long intervalTime(Date firstTime, Date endTime) {
        return Math.abs((firstTime.getTime() - endTime.getTime()) / 1000);
    }

    /**
     * 按指定类型返回连个时间的间隔，不整除则舍余
     */
    public static Integer intervalTime(Date firstTime, Date endTime, String resultType) {
        long second = Math.abs((firstTime.getTime() - endTime.getTime()) / 1000);
        String type = resultType.toLowerCase();
        if ("minute".equals(type)) {
            return new Long(second / 60L).intValue();
        } else if ("hour".equals(type)) {
            return new Long(second / (60L * 60)).intValue();
        } else if ("day".equals(type)) {
            return new Long(second / (60L * 60 * 24)).intValue();
        } else if ("month".equals(type)) {
            return new Long(second / (60L * 60 * 24 * 30)).intValue();
        } else if ("year".equals(type)) {
            return new Long(second / (60L * 60 * 24 * 365)).intValue();
        }
        return new Long(second).intValue();
    }

    public static int getAgeByBirth(Date birthday) {
        try {
            int age = 0;
            Calendar born = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            if (birthday != null) {
                now.setTime(new Date());
                born.setTime(birthday);
                if (born.after(now)) {
                    throw new IllegalArgumentException("年龄不能超过当前日期");
                }
                age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
                int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
                if (nowDayOfYear < bornDayOfYear) {
                    age -= 1;
                }
            }
            return age;
        }catch (Exception e){
            return 0;
        }

    }

    /**
     * 比较两个时间先后
     * @param d1
     * @param d2
     * @return  1：d1在d2之后   -1：d1在d2之前    0：d1等于d2
     */
    public static int compareDate(Date d1,Date d2){
        if (d1.getTime() > d2.getTime()) {
            return 1;
        } else if (d1.getTime() < d2.getTime()) {
            return -1;
        } else {//相等
            return 0;
        }
    }

    /**根据日期获取星座*/
    public static String getAstro(Date d) {
        if (d == null){
            return null;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(d);
        int month = date.get(Calendar.MONTH) + 1;//获取月
        int day = date.get(Calendar.DAY_OF_MONTH);//获取日
        String[] astro = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座",
                "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};
        int[] arr = new int[]{20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};// 两个星座分割日
        int index = month;
        // 所查询日期在分割日之前，索引-1，否则不变
        if (day < arr[month - 1]) {
            index = index - 1;
        }
        // 返回索引指向的星座string
        return astro[index];
    }

//    public static void main(String[] args) {
//        if (DateUtil.compareDate(DateUtil.parseDate("2017-8-25", "yyyy-MM-dd"),new Date()) <= 0 && DateUtil.compareDate(new Date(),DateUtil.parseDate("2017-8-26", "yyyy-MM-dd")) <= 0){
//            System.out.println("活动期间");
//        }

//        System.out.println(DateUtil.intervalTime(DateUtil.parseDate("2017-8-25", "yyyy-MM-dd"),DateUtil.parseDate("2017-8-26", "yyyy-MM-dd")));

//    }


    /**
     * 判断时间是不是今天
     * @param date
     * @return    是返回true，不是返回false
     */
    public static boolean isNow(Date date,String format) {
        //当前时间
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat(format);
        //获取今天的日期
        String nowDay = sf.format(now);


        //对比的时间
        String day = sf.format(date);

        return day.equals(nowDay);
    }

    /**
     * 判断是否在指定时间内
     * @param startDate 开始事件
     * @param endDate 结束时间
     * @param date 指定时间
     */
    public static boolean isBetweenPeriod(Date startDate,Date endDate,Date date){
        return startDate != null && endDate != null && DateUtil.compareDate(startDate,date) <= 0 && DateUtil.compareDate(date, endDate) <= 0;
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(getMinuteUp(parseDate("16:43:44","HH:mm:ss"),parseDate("16:41:40","HH:mm:ss")));

    }


}
