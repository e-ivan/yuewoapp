package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.UserBlack;

/**
 * Created by Administrator on 2017/1/5.
 */
public interface UserBlackService {
    void addUserBlack(String fkUserId,String blackUserId);
    void removeUserBlack(String fkUserId,String blackUserId);
    UserBlack findUserBlack(String fkUserId,String blackUserId);
    CutPage<UserBlack> queryMyUserBlack(String fkUserId,Integer page,Integer limit);
}
