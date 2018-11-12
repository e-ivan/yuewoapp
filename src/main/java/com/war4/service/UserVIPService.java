package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.UserVIP;

/**
 * Created by Administrator on 2016/12/26.
 */
public interface UserVIPService {

    void saveUserVIP(String fkUserId,String vip,Integer integral);

    UserVIP queryUserVipById(String fkUserId);

    void addIntegral(String fkUserId,Integer integral);

    CutPage<UserVIP> queryVIPListBySettingId(String setting);
}
