package com.war4.service.impl;

import com.war4.pojo.UserAccessToken;
import com.war4.repository.BaseRepository;
import com.war4.service.UserAccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * Created by dly on 2016/11/29.
 */
@Service
public class UserAccessTokenServiceImpl implements UserAccessTokenService {
    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public UserAccessToken insertAccessToken(String userId) {
        UserAccessToken userAccessToken = new UserAccessToken();
        userAccessToken.setFkUserId(userId);
        userAccessToken.setAccessToken(UUID.randomUUID().toString());
        userAccessToken.setUpdateTime(new Date());
        baseRepository.saveObj(userAccessToken);
        return userAccessToken;
    }

    @Override
    public UserAccessToken getAccessTokenByUserId(String userAccessTokenId) {
        return baseRepository.getObjById(UserAccessToken.class,userAccessTokenId);
    }

    @Override
    @Transactional
    public void updateAccessToken(UserAccessToken userAccessToken) {
        baseRepository.updateObj(userAccessToken);
    }

    @Override
    @Transactional
    public void refreshAccessToken(String userId) {
        UserAccessToken userAccessToken = getAccessTokenByUserId(userId);
        userAccessToken.setAccessToken(UUID.randomUUID().toString());
        userAccessToken.setUpdateTime(new Date());
        updateAccessToken(userAccessToken);
    }
}
