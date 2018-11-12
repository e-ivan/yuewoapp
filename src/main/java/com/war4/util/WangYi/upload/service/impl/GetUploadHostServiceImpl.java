package com.war4.util.WangYi.upload.service.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.war4.util.WangYi.VcloudException;
import com.war4.util.WangYi.upload.param.GetUploadHostParam;
import com.war4.util.WangYi.upload.service.GetUploadHostService;
import com.war4.util.WangYi.upload.util.UploadUtil;


/**
* <p>Title: GetUploadHostServiceImpl</p>
* <p>Description: 获取上传加速节点地址的实现类</p>
* <p>Company: com.war4.util.WangYi</p>
* @date       2016-6-21
*/
public class GetUploadHostServiceImpl implements GetUploadHostService {

	/** 日志实例*/
	public static final Logger logger = Logger.getLogger(GetUploadHostServiceImpl.class);
	
		
	/* 
	* <p>Title: getUploadHost</p>
	* <p>Description: </p>
	* @param bucket
	* @return
	* @throws IOException
	* @throws VcloudException
	* @see com.war4.util.WangYi.upload.service.GetUploadHostService#getUploadHost(java.lang.String)
	*/
	public GetUploadHostParam getUploadHost(String bucket) throws IOException, VcloudException{
		if(null == bucket || "".equals(bucket.trim()))
			throw new VcloudException("bucket is null or invalid");
		UploadUtil util = new UploadUtil(bucket);
		GetUploadHostParam getUploadHostParam = util.getUploadHost();
		return getUploadHostParam;

	}
}
