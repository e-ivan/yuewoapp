package com.war4.service;

import com.war4.pojo.UserRatio;

/**
 * 用户占比服务
 * Created by E_Iva on 2018.1.22.0022.
 */
public interface UserRatioService {
    void save(UserRatio ratio);
    UserRatio getLatestByUser(String userId);
}
