package com.war4.util.WangYi.upload.demo;


import com.war4.util.WangYi.auth.BasicCredentials;
import com.war4.util.WangYi.auth.Credentials;
import com.war4.util.WangYi.client.VcloudClient;
import com.war4.util.WangYi.upload.param.QueryVideoIDorWatermarkIDParam;
import com.war4.util.WangYi.util.FileUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>Title: UploadVideoDemo</p>
 * <p>Description: 简单的视频上传的Demo</p>
 * <p>Company: com.war4.util.WangYi</p>
 *
 * @date 2016-6-22
 */
public class UploadVideoDemo {

    /**
     * 日志实例
     */
    public static final Logger logger = Logger.getLogger(UploadVideoDemo.class);


    public static void main(String[] args) {
         /* 输入个人信息 */
         /* 开发者平台分配的appkey 和 appSecret */
        String appKey = "f945118ea0064892aeafc499e34c6f77";
        String appSecret = "2c28824084e949f484c9dda5793d6e85";

        Credentials credentials;
        credentials = new BasicCredentials(appKey, appSecret);
        VcloudClient vclient = new VcloudClient(credentials);


        try {
            /*请输入上传文件路径*/
            String filePath = "C:\\Users\\Administrator\\Desktop\\千年影院\\3ed15d3d-e05c-49eb-aee7-22f10de6db25.mp4";
            //String filePath = "e:\\image_20160711145925.png";

            Map<String, Object> initParamMap = new HashMap<String, Object>();
			/*输入上传文件的相关信息 */
			/* 上传文件的原始名称（包含后缀名） 此参数必填*/
            initParamMap.put("originFileName", FileUtil.getFileName(filePath));

		     /* 用户命名的上传文件名称  此参数非必填*/
            //initParamMap.put("userFileName", "for_love.mp4");

		     /* 视频所属的类别ID（不填写为默认分类）此参数非必填*/
            // initParamMap.put("typeId", new Long(1056));

		     /* 频所需转码模板ID（不填写为默认模板） 此参数非必填*/
            // initParamMap.put("presetId", new Long(2007));

		     /* 转码成功后回调客户端的URL地址（需标准http格式）  此参数非必填*/
            //initParamMap.put("callbackUrl", "");

		     /* 上传视频的描述信息  此参数非必填*/
            //initParamMap.put("description", "love.mp4");
		     
		     /* 上传视频的视频水印Id 此参数非必填*/
            //initParamMap.put("watermarkId", new Long(1));

            /** 上传成功后回调客户端的URL地址（需标准http格式）   此参数非必填*/

            //initParamMap.put("uploadCallbackUrl", "");

            /** 用户自定义信息，会在上传成功或转码成功后通过回调返回给用户    此参数非必填 */
            // initParamMap.put("userDefInfo", null);


            QueryVideoIDorWatermarkIDParam queryVideoIDParam = vclient.uploadVideo(filePath, initParamMap);
            if (null != queryVideoIDParam) {
                System.out.println("[UploadDemo] upload video successfully and the vid is " + queryVideoIDParam.getRet().getList().get(0).getVid());
                System.out.println("[UploadDemo] upload video successfully and the imgid is " + queryVideoIDParam.getRet().getList().get(0).getImgId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
