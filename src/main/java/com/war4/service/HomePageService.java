package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.HomePage;

/**
 * Created by Administrator on 2016/12/7.
 */
public interface HomePageService {
    HomePage addHomePage(HomePage url);
    void deleteHomePage(String id);
    CutPage<HomePage> queryHomePageList(String userId,Integer page,Integer limit);

//    void saveHomePageSharePicUrl(String pid);
}
