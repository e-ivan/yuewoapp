package com.war4.util.WangYi.upload.demo;

import com.war4.util.WangYi.auth.BasicCredentials;
import com.war4.util.WangYi.auth.Credentials;
import com.war4.util.WangYi.client.VcloudClient;
import com.war4.util.WangYi.upload.param.QueryVideoIDorWatermarkIDParam;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: QueryVideoIDDemo</p>
 * <p>Description: 上传完成后查询视频主ID的Demo</p>
 * <p>Company: com.war4.util.WangYi</p>
 *
 * @date 2016-6-21
 */
public class QueryVideoIDDemo {


    /**
     * 日志实例
     */
    public static final Logger logger = Logger.getLogger(QueryVideoIDDemo.class);

    public static void main(String[] args) {

		/* 输入个人信息 */
        /* 开发者平台分配的appkey 和 appSecret */
        String appKey = "f945118ea0064892aeafc499e34c6f77";
        String appSecret = "2c28824084e949f484c9dda5793d6e85";

        Credentials credentials;
        credentials = new BasicCredentials(appKey, appSecret);
        VcloudClient vclient = new VcloudClient(credentials);

		/* 请输入 查询文件的对象名     参数必填*/
        List<String> objectNamesList = new ArrayList<String>();
        objectNamesList.add("3ed15d3d-e05c-49eb-aee7-22f10de6db25.mp4");
        //objectNamesList.add("14e36114-13f8-48f4-b7a2-90b1b76c531c.mp4");

        try {
			/*上传完成后查询视频主ID返回结果的封装类*/
            QueryVideoIDorWatermarkIDParam queryVideoIDParam = vclient.queryVideoID(objectNamesList);

            if (queryVideoIDParam.getCode() == 200) {
                System.out.println("[InitUploadVideoDemo] query videoID successfully. " + queryVideoIDParam.getRet().getList().toString());
            } else {
                System.out.println("[InitUploadVideoDemo] fail to query videoID. " + "return code " + queryVideoIDParam.getCode() + " return message " + queryVideoIDParam.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
