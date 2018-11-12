package com.war4.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 文本工具类
 * Created by E_Iva on 2018.3.13.0013.
 */
public class TextUtil {
    private TextUtil() {
    }

    /**
     * 打码昵称
     * @param nickname
     * @return
     */
    public static String codingNickname(String nickname){
        if (StringUtils.isEmpty(nickname) || nickname.length() < 2) {
            return nickname;
        }
        StringBuilder sb = new StringBuilder(nickname);
        return sb.replace(1, sb.length() - 1, "***").toString();
    }
}
