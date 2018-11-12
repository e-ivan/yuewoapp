package com.war4.service;

import net.sf.json.JSONObject;

/**
 * 通用服务
 * Created by hh on 2017.6.8 0008.
 */
public interface CommonService {
    /**
     * 根据城市名称获取城市数据
     * @param cityName
     * @return
     */
    JSONObject queryCityByName(String cityName) throws Exception;
}
