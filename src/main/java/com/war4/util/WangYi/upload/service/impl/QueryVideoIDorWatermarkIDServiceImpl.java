package com.war4.util.WangYi.upload.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.war4.util.WangYi.VcloudException;
import com.war4.util.WangYi.upload.module.QueryVideoIDorWatermarkIDModule;
import com.war4.util.WangYi.upload.param.QueryVideoIDorWatermarkIDParam;
import com.war4.util.WangYi.upload.service.QueryVideoIDorWatermarkIDService;
import com.war4.util.WangYi.upload.util.UploadUtil;


/**
* <p>Title: QueryVideoIDorWatermarkIDServiceImpl</p>
* <p>Description:  查询视频主ID或水印图片主ID的实现类</p>
* <p>Company: com.war4.util.WangYi</p>
* @date       2016-6-21
*/
public class QueryVideoIDorWatermarkIDServiceImpl implements QueryVideoIDorWatermarkIDService {

	/** 日志实例*/
	public static final Logger logger = Logger.getLogger(QueryVideoIDorWatermarkIDServiceImpl.class);
	
	/* 
	* <p>Title: queryVideoID</p>
	* <p>Description: </p>
	* @param objectNamesList
	* @return
	* @throws IOException
	* @throws VcloudException
	* @see com.war4.util.WangYi.upload.util.service.impl.QueryVideoIDService#queryVideoID(java.util.List)
	*/
	public QueryVideoIDorWatermarkIDParam queryVideoID(List<String> objectNamesList) throws IOException, VcloudException{
		
		if(null == objectNamesList || objectNamesList.size() < 1)
			throw new VcloudException("objectNamesList is null or invalid");
		QueryVideoIDorWatermarkIDModule queryVideoIDorWatermarkIDModule = new QueryVideoIDorWatermarkIDModule(objectNamesList);
		UploadUtil util = new UploadUtil(queryVideoIDorWatermarkIDModule);		
		QueryVideoIDorWatermarkIDParam queryVideoIDParam = util.queryVideoID();
		return queryVideoIDParam;

	}
}
