package com.war4.util;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

/**
 * 汉字工具类
 * Created by E_Iva on 2018.4.26.0026.
 */
public class ChineseCharUtil {

    /**
     * 转换为有声调的拼音字符串
     *
     * @param pinYinStr 汉字
     * @return 有声调的拼音字符串
     */
    public static String changeToMarkPinYin(String pinYinStr,String separator) {
        String tempStr = null;
        try {
            tempStr = PinyinHelper.convertToPinyinString(pinYinStr, separator, PinyinFormat.WITH_TONE_MARK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStr;
    }
    public static String changeToMarkPinYin(String pinYinStr) {
        return changeToMarkPinYin(pinYinStr,"");
    }

    /**
     * 转换为数字声调字符串
     *
     * @param pinYinStr 需转换的汉字
     * @return 转换完成的拼音字符串
     */
    public static String changeToNumberPinYin(String pinYinStr,String separator) {
        String tempStr = null;
        try {
            tempStr = PinyinHelper.convertToPinyinString(pinYinStr, separator, PinyinFormat.WITH_TONE_NUMBER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStr;
    }
    public static String changeToNumberPinYin(String pinYinStr) {
        return changeToNumberPinYin(pinYinStr,"");
    }

    /**
     * 转换为不带音调的拼音字符串
     *
     * @param pinYinStr 需转换的汉字
     * @param separator 分割符
     * @return 拼音字符串
     */
    public static String changeToTonePinYin(String pinYinStr, String separator) {
        String tempStr = null;
        try {
            tempStr = PinyinHelper.convertToPinyinString(pinYinStr, separator, PinyinFormat.WITHOUT_TONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStr;
    }

    public static String changeToTonePinYin(String pinYinStr) {
        return changeToTonePinYin(pinYinStr,"");
    }

    /**
     * 转换为每个汉字对应拼音首字母字符串
     *
     * @param pinYinStr 需转换的汉字
     * @return 拼音字符串
     */
    public static String changeToGetShortPinYin(String pinYinStr) {
        String tempStr = null;
        try {
            tempStr = PinyinHelper.getShortPinyin(pinYinStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStr;
    }

    /**
     * 检查汉字是否为多音字
     *
     * @param pinYinStr 需检查的汉字
     * @return true 多音字，false 不是多音字
     */
    public static boolean checkPinYin(char pinYinStr) {
        boolean check = false;
        try {
            check = PinyinHelper.hasMultiPinyin(pinYinStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    /**
     * 简体转换为繁体
     *
     * @param pinYinStr
     * @return
     */
    public static String changeToTraditional(String pinYinStr) {
        String tempStr = null;
        try {
            tempStr = ChineseHelper.convertToTraditionalChinese(pinYinStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStr;
    }

    /**
     * 繁体转换为简体
     *
     * @param pinYinSt
     * @return
     */
    public static String changeToSimplified(String pinYinSt) {
        String tempStr = null;
        try {
            tempStr = ChineseHelper.convertToSimplifiedChinese(pinYinSt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStr;
    }
}
