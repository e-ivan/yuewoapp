package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.UserVipSetting;

/**
 * Created by Administrator on 2017/1/6.
 */
public interface UserVipSettingService {
    void addUserVipSetting(UserVipSetting userVipSetting);
    void updateUserVipSetting(UserVipSetting userVipSetting);
    void deleteUserVipSetting(String settingId);
    CutPage<UserVipSetting> queryUserVipSetting();
}
