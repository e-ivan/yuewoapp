package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.SeceenwriterResume;

/**
 * Created by Administrator on 2016/12/21.
 */
public interface SeceenwriterResumeService {
    SeceenwriterResume saveSeceenwriterResume(SeceenwriterResume seceenwriterResume);
    SeceenwriterResume querySeceenwriterResumeForId(String fkUserId);
    CutPage<SeceenwriterResume> querySeceenwriterResumeList(Integer page,Integer limit);
}
