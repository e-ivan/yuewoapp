package com.war4.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dly on 2016/8/31.
 */
public class NumberUtil {

    private NumberUtil() {
    }

    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * @功能: 10进制串转为BCD码
     * @参数: 10进制串
     * @结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    //获取制定长度的随机字数字字符串
    public static String getRandomNumStrByLength(Integer length) {
        String num = "";
        for (int i = 0; i < length; i++) {
            Integer integer = new Random().nextInt(10);
            num += integer;
        }
        return num;
    }


//    /**
//     * 分转换为元.
//     *
//     * @param fen
//     *
//     * @return
//     */
//    public static String fromFenToYuan(final String fen) {
//        String yuan = "";
//        final int MULTIPLIER = 100;
//        Pattern pattern = Pattern.compile("^[1-9][0-9]*{1}");
//        Matcher matcher = pattern.matcher(fen);
//        if (matcher.matches()) {
//            yuan = new BigDecimal(fen).divide(new BigDecimal(MULTIPLIER)).setScale(2).toString();
//        } else {
//            System.out.println("参数格式不正确!");
//        }
//        return yuan;
//    }

    /**
     * 元转换为分.
     *
     * @param yuan
     * @return
     */
    public static String fromYuanToFen(BigDecimal yuan) {
        //保留两位
        yuan = yuan.setScale(2, BigDecimal.ROUND_HALF_UP);
        //乘100
        Integer fen = yuan.multiply(new BigDecimal(100)).intValue();
        return fen.toString();
    }

    /**
     * 获取一个范围间的随机值
     *
     * @param maxVal
     * @param minVal
     * @return
     */
    public static String getAroundVal(String maxVal, String minVal, int scale) {
        //如果最小值大于等于最大值，返回最小值
        if (new BigDecimal(maxVal).compareTo(new BigDecimal(minVal)) <= 0) {
            return minVal;
        }
        //max * [0,1] % (max - min) + min
        //差值
        BigDecimal sub = new BigDecimal(maxVal).subtract(new BigDecimal(minVal));
        //范围随机数
        BigDecimal multiply = new BigDecimal(maxVal).multiply(new BigDecimal(Math.random()));
        //取范围之间的值
        BigDecimal aroundVal = multiply.remainder(sub, MathContext.UNLIMITED).add(new BigDecimal(minVal)).setScale(scale, RoundingMode.HALF_UP);
        return aroundVal.toString();
    }

    public static Double getAroundVal(Double maxVal, Double minVal, int scale) {
        //如果最小值大于等于最大值，返回最小值
        if (new BigDecimal(maxVal).compareTo(new BigDecimal(minVal)) <= 0) {
            return minVal;
        }
        //max * [0,1] % (max - min) + min
        //差值
        BigDecimal sub = new BigDecimal(maxVal).subtract(new BigDecimal(minVal));
        //范围随机数
        BigDecimal multiply = new BigDecimal(maxVal).multiply(new BigDecimal(Math.random()));
        //取范围之间的值
        BigDecimal aroundVal = multiply.remainder(sub, MathContext.UNLIMITED).add(new BigDecimal(minVal)).setScale(scale, RoundingMode.HALF_UP);
        return aroundVal.doubleValue();
    }

    /**
     * 随机指定范围内N个不重复的数
     * 在初始化的无重复待选数组中随机产生一个数放入结果中，
     * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换
     * 然后从len-2里随机产生下一个随机数，如此类推
     *
     * @param max 指定范围最大值
     * @param min 指定范围最小值
     * @param n   随机数个数
     * @return int[] 随机数结果集
     */
    public static List<Integer> randomArray(int min, int max, int n) {
        int len = max - min + 1;

        List<Integer> resultList = new ArrayList<>();
        if (max < min || n > len) {
            return resultList;
        }

        //初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min + len; i++) {
            source[i - min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index;
        for (int i = 0; i < result.length; i++) {
            //待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            //将随机到的数放入结果集
            result[i] = source[index];
            //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }


        for (int aResult : result) {
            resultList.add(aResult);
        }

        return resultList;
    }

    //判断是否为数字
    public static boolean isNumeric(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
    public static boolean isDecimals(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        boolean isInt = Pattern.compile("^-?[1-9]\\d*$").matcher(str).find();
        boolean isDouble = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();
        return isInt || isDouble;
    }



    public static void main(String args[]) {
        System.out.println(isNumeric(null));
    }

}


