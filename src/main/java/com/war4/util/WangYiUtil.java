package com.war4.util;

import com.war4.base.BusinessException;
import com.war4.base.SystemParameters;
import com.war4.enums.CommonErrorResult;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/8.
 */
public class WangYiUtil {
    public static String commonPost(String url,StringEntity params) throws Exception{
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(url);

        String appKey = SystemParameters.WANGYI_APP_KEY;
        String appSecret = SystemParameters.WANGYIAPP_SECRET;
        String nonce =  "1";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");

        // 设置请求的参数
        httpPost.setEntity(params);

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        JSONObject jsonObject = JSONObject.fromObject(result);
          String code = jsonObject.getString("code");
//        String msg  = jsonObject.getString("msg");
        if(!code.equals("200")){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"");
        }


        return result;
    }

//    //JNI
//    public native int stream(String inputurl, String outputurl);
//
//    static{
//        System.out.println(System.getProperty("java.library.path"));
//        String realPath = new File("D:\\workspace\\2016_11_千年影业\\03开发\\032程序源码\\qnyy\\jni\\libavutil-54.so").getAbsolutePath() ;
//        System.load(realPath);
//        System.load("avutil-54");
//        System.loadLibrary("swresample-1");
//        System.loadLibrary("avcodec-56");
//        System.loadLibrary("avformat-56");
//        System.loadLibrary("swscale-3");
//        System.loadLibrary("postproc-53");
//        System.loadLibrary("avfilter-5");
//        System.loadLibrary("avdevice-56");
//        System.loadLibrary("sffstreamer");
//    }
//
//    public static void main(String[] args){
//
//       new WangYiUtil().stream("","");
//
//    }
}
