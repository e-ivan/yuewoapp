package com.war4.util.WangYi.upload.service;

import java.io.IOException;
import java.util.Map;

import com.war4.util.WangYi.VcloudException;
import com.war4.util.WangYi.upload.param.InitUploadVideoParam;

/**
* <p>Title: InitUploadVideoService</p>
* <p>Description: 视频上传初始化接口</p>
* <p>Company: com.war4.util.WangYi</p>
* @date       2016-6-30
*/
public interface InitUploadVideoService {

	/**
	 * 
	 * <p>Title: initUploadVideo</p>
	 * <p>Description: 视频上传初始化</p>
	 * @param initParamMap 上传文件的相关信息集合
	 * @return initUploadVideoParam 视频上传初始化返回结果的封装类
	 * @throws VcloudException 
	 * @throws IOException 
	 */
	public abstract InitUploadVideoParam initUploadVideo(
			Map<String, Object> initParamMap) throws IOException,
			VcloudException;

}