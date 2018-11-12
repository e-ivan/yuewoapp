package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.RecruitInfo;

/**
 * Created by Administrator on 2016/12/22.
 */
public interface RecruitInfoService {
    RecruitInfo saveRecruitInfo(RecruitInfo recruitInfo);
    RecruitInfo queryRecruitInfoById(String recruitInfoId);
    void deleteRecruitInfoById(String recruitInfoId);
    CutPage<RecruitInfo> queryRecruitInfoListForCrew(String crewId,Integer page,Integer limit);
}
