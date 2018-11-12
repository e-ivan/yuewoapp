package com.war4.util.WangYi.upload.service.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.war4.util.WangYi.VcloudException;
import com.war4.util.WangYi.upload.module.SetCallbackModule;
import com.war4.util.WangYi.upload.param.SetCallbackParam;
import com.war4.util.WangYi.upload.service.SetCallbackService;
import com.war4.util.WangYi.upload.util.UploadUtil;

/**
 * 
* <p>Title: SetCallbackServiceImpl</p>
* <p>Description:  设置上传回调地址的实现类</p>
* <p>Company: com.war4.util.WangYi</p>
* @date       2016-8-28
 */
public class SetCallbackServiceImpl implements SetCallbackService {

	/** 日志实例*/
	public static final Logger logger = Logger.getLogger(SetCallbackServiceImpl.class);
	
	/* 
	* <p>Title: setCallback</p>
	* <p>Description: </p>
	* @param callbackUrl
	* @return
	* @throws IOException
	* @throws VcloudException
	* @see com.war4.util.WangYi.upload.service.impl.SetCallbackService#setCallback(java.lang.String)
	*/
	public SetCallbackParam setCallback(String callbackUrl) throws IOException, VcloudException{
		SetCallbackModule setCallbackModule = new SetCallbackModule(callbackUrl);
		UploadUtil util = new UploadUtil(setCallbackModule);
		SetCallbackParam setCallbackParam = util.setCallback();
		return setCallbackParam;
	}
}
