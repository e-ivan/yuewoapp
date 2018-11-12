package com.war4.service;

import com.war4.pojo.UserAccessToken;

/**
 * Created by dly on 2016/11/29.
 */
public interface UserAccessTokenService {
    //初始化accessToken
    UserAccessToken insertAccessToken(String userId);
    //查询accessToken
    UserAccessToken getAccessTokenByUserId(String userAccessTokenId);
    //更新accessToken
    void updateAccessToken(UserAccessToken userAccessToken);
    //刷新accessToken
    void refreshAccessToken(String userId);
}
