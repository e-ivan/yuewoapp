package com.war4.service;

import com.war4.pojo.SystemParameter;

/**
 * Created by Administrator on 2016/12/29.
 */
public interface SystemParameterService {
    SystemParameter querySystemParameter();
    void updateSystemParameter(String servicePhone,String aboutUs,String help);
}
