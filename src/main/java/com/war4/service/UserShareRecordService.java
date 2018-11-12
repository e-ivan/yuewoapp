package com.war4.service;

/**
 * 用户分享服务
 * Created by E_Iva on 2018.1.4.0004.
 */
public interface UserShareRecordService {
    void saveShare(String userId,String url,Integer type,String objectId);
    //查询是否分享指定内容
    long checkShare(String userId,String objectId,Integer type);
}
