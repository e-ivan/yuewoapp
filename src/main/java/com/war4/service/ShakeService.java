package com.war4.service;

//import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.war4.pojo.UserInfoBase;

/**
 * Created by Administrator on 2016/12/28.
 */
public interface ShakeService {
    UserInfoBase saveShakeAndGetPerple(String fkUserId, String x, String y) throws InterruptedException;
}
