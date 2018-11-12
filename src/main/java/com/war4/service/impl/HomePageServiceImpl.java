package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.HomePage;
import com.war4.repository.BaseRepository;
import com.war4.service.AdPageService;
import com.war4.service.AssembleService;
import com.war4.service.HomePageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/7.
 */
@Service
public class HomePageServiceImpl implements HomePageService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;
    @Autowired
    private AdPageService adPageService;

    @Override
    @Transactional
    public HomePage addHomePage(HomePage page) {
        HomePage homePage = new HomePage();
        homePage.setTitle(page.getTitle());
        homePage.setContent(page.getContent());
        homePage.setId(UUID.randomUUID().toString());
        homePage.setUrl(page.getUrl().trim());
        homePage.setMovieId(page.getMovieId());
        homePage.setType(page.getType());
        homePage.setActivityId(page.getActivityId());
        homePage.setLocation(page.getLocation());
        baseRepository.saveObj(homePage);
        return homePage;
    }

    @Override
    @Transactional
    public void deleteHomePage(String id) {
        HomePage homePage = baseRepository.getObjById(HomePage.class,id);
        if(homePage==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "您要删除的首页广告不存在!");
        }
        baseRepository.logicDelete(homePage);
    }

    @Override
    public CutPage<HomePage> queryHomePageList(String userId,Integer page, Integer limit) {

        String hql = baseRepository.getBaseHqlByClass(HomePage.class);

        CutPage<HomePage> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);

        for(HomePage homePage:cutPage.getiData()){
            if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(homePage.getUrl())) {
                homePage.setUrl(homePage.getUrl() + "?userId=" + userId);
            }
            assembleService.assembleObject(homePage);
        }
        return cutPage;
    }


//    @Override
//    @Transactional
//    public void saveHomePageSharePicUrl(String pid){
//
//        if(pid==null){
//            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "广告id不存在");
//        }
//        String hql = "from HomePage where id='"+pid+"' ";
//        HomePage homePage = baseRepository.queryUniqueData(hql);
//        HomePage homePage1 =  assembleService.assembleObject(homePage);
//        baseRepository.saveObj(homePage1);
//    }

}
