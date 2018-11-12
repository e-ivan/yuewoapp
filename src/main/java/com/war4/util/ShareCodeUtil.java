package com.war4.util;

import java.util.Random;

/**
 * Created by Administrator on 2016/11/1.
 */

public class ShareCodeUtil {

    /** 自定义进制(0,1没有加入,容易与o,l混淆) */
    private static final char[] r=new char[]{'q','w', 'e','8', 'a', 's', '2', 'd','z', 'x', '9', 'c', '7', 'p', '5', 'i','k' ,'3', 'm','j', 'u', 'f','r', '4', 'v' ,'y','l','t','n','6', 'b','g','h'};
    private static final char[] upR =new char[]{'q','Q', 'w', 'e','E', '8', 'a','A', 's', '2', 'd','D', 'z', 'x', '9', 'c', '7', 'p','P', '5', 'i','I' ,'k' ,'3', 'm','M' ,'j', 'J','u', 'U','f','F' ,'r', 'R','4', 'v' ,'y', 'Y','l','L' ,'t', 'T','n','N', '6', 'b', 'B','g', 'G','h','H'};

    /** (不能与自定义进制有重复) */
    private static final char b='o';

    /** 进制长度 */
    private static final int binLen=r.length;
    private static final int upBinLen= upR.length;

    /** 序列最小长度 */
    private static final int s=6;

    /**
     * 根据ID生成六位随机码
     * @param id ID
     * @return 随机码
     */
    public static String toSerialCode(long id, int strlong, char[] chars,int length) {
        char[] buf=new char[128];
        int charPos=128;

        while((id / length) > 0) {
            int ind=(int)(id % length);
            // System.out.println(num + "-->" + ind);
            buf[--charPos]=chars[ind];
            id /= length;
        }
        buf[--charPos]=chars[(int)(id % length)];
        // System.out.println(num + "-->" + num % binLen);
        String str=new String(buf, charPos, (128 - charPos));
        // 不够长度的自动随机补全
        if(str.length() < strlong) {
            StringBuilder sb=new StringBuilder();
            sb.append(b);
            Random rnd=new Random();
            for(int i=1; i < strlong - str.length(); i++) {
                sb.append(chars[rnd.nextInt(length)]);
            }
            str+=sb.toString();
        }
        return str;
    }




    public static long codeToId(String code) {
        char chs[]=code.toCharArray();
        long res=0L;
        for(int i=0; i < chs.length; i++) {
            int ind=0;
            for(int j=0; j < binLen; j++) {
                if(chs[i] == r[j]) {
                    ind=j;
                    break;
                }
            }
            if(chs[i] == b) {
                break;
            }
            if(i > 0) {
                res=res * binLen + ind;
            } else {
                res=ind;
            }
            // System.out.println(ind + "-->" + res);
        }
        return res;
    }

    public static String getShareCode(){
        Long i = Math.round(Math.ceil(Math.random()*500000+500000));
        return ShareCodeUtil.toSerialCode(i,s,r,binLen);
    }
    public static String getCodeByLong(int strLong,boolean hasUpper){
        Long i = Math.round(Math.ceil(Math.random()*500000+500000));
        if (hasUpper){
            return ShareCodeUtil.toSerialCode(i,strLong,upR,upBinLen);
        }
        return ShareCodeUtil.toSerialCode(i,strLong,r,binLen);
    }
    public static void main(String[] arg){

        System.out.println(ShareCodeUtil.getCodeByLong(6,true));
    }
}