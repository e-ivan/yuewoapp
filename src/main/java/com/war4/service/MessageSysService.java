package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.MessageSys;

/**
 * Created by Administrator on 2017/1/9.
 */
public interface MessageSysService {
    void addSyaMessageSys(MessageSys messageSys);
    void addPersonMessageSys(MessageSys messageSys);
    CutPage<MessageSys> queryUserMessageSys(String userId,Integer page,Integer limit);
    CutPage<MessageSys> querySysMessageSys(Integer page,Integer limit);
}
