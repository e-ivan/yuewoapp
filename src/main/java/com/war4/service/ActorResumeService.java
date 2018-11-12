package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.ActorResume;

/**
 * Created by Administrator on 2016/12/21.
 */
public interface ActorResumeService {
    ActorResume saveActorResume(ActorResume actorResume);
    ActorResume queryActorResume(String fkUserId);
    CutPage<ActorResume> queryActorResumeList(Integer page,Integer limit);
}
