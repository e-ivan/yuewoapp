package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.DatingFilm;
import com.war4.pojo.UserEvaluate;
import com.war4.vo.DatingUserLatestVO;
import com.war4.vo.DatingUserVO;
import com.war4.vo.QueryDatingUserVO;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 */
public interface DatingFilmService {

    void addDatingFilm(DatingFilm datingFilm);

    DatingFilm queryDatingFilmForId(String datingId);

    CutPage<DatingFilm> queryMyDating(String userId,Integer type,Integer page,Integer limit);

    DatingFilm updateDatingSettingAccept(String datingId);

    void updateDatingSettingRefuse(String datingId);

    void deleteDatingSetting(String datingId);

    CutPage<DatingUserVO> queryDatingUser(QueryDatingUserVO vo);
    //最新方案
    CutPage<DatingUserLatestVO> queryDatingUserLatest(QueryDatingUserVO vo);

    long queryDatingUserCount(QueryDatingUserVO vo);

    List<DatingUserLatestVO> queryUserImageAlbumCount(List<DatingUserLatestVO> users);

    //评价约会人
    void evaluateDating(String userId,String appraiser,Float fenShu,String content);
    //
    CutPage<UserEvaluate> queryUserEvaluate(String userId,Integer page,Integer limit);

    CutPage<DatingFilm> queryUserAllDatingFilm(String userId);

    CutPage<DatingFilm>queryAllDating(String status,String keyword,Integer page,Integer limit);



}
