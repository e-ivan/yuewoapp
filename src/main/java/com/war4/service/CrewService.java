package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.Crew;

/**
 * Created by Administrator on 2016/12/22.
 */
public interface CrewService {
    Crew saveCrew(Crew crew);
    Crew queryCrewById(String crewId);
    void deleteCrewById(String crewId);
    CutPage<Crew> queryCrewList(Integer page,Integer limit);
}
