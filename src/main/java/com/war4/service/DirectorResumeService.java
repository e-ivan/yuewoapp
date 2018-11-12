package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.DirectorResume;

/**
 * Created by Administrator on 2016/12/21.
 */
public interface DirectorResumeService {
    DirectorResume saveDirectorResume(DirectorResume directorResume);
    DirectorResume queryDirectorResumeById(String fkUserId);
    CutPage<DirectorResume> queryDirectorResumeList(Integer page ,Integer limit);
}
