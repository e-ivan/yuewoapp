package com.war4.util.WangYi.upload.service;

import java.io.IOException;

import com.war4.util.WangYi.VcloudException;
import com.war4.util.WangYi.upload.param.SetCallbackParam;

/**
 * 
* <p>Title: SetCallbackService</p>
* <p>Description: 设置上传回调地址的接口</p>
* <p>Company: com.war4.util.WangYi</p>
* @date       2016-8-28
 */
public interface SetCallbackService {

	/**
	 * 
	 * <p>Title: setCallback</p>
	 * <p>Description: 设置上传回调地址</p>
	 * @param callbackUrl  上传成功后回调客户端的URL地址
	 * @return setCallbackParam  设置上传回调地址接口输出参数的封装类
	 * @throws IOException
	 * @throws VcloudException
	 */
	public abstract SetCallbackParam setCallback(String callbackUrl)
			throws IOException, VcloudException;

}