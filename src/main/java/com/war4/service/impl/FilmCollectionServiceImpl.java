package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CollectState;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.FilmCollection;
import com.war4.pojo.UserCorrelative;
import com.war4.repository.BaseRepository;
import com.war4.service.FilmCollectionService;
import com.war4.service.KouDianYingService;
import com.war4.service.UserCorrelativeService;
import com.war4.vo.film.MovieVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Administrator on 2017/1/4.
 */
@Service
public class FilmCollectionServiceImpl implements FilmCollectionService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private UserCorrelativeService correlativeService;

    @Autowired
    private KouDianYingService kouDianYingService;

    @Override
    @Transactional
    public void addFilmCollection(String fkUserId, String fkFilmId) throws Exception {
        if (StringUtils.isBlank(fkUserId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "fkFilmId不能为空！");
        }
        if (StringUtils.isBlank(fkFilmId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "fkUserId不能为空！");
        }
        FilmCollection fm = checkFilmIsCollection(fkUserId, fkFilmId);
        if (fm != null) {
            if (fm.getState() == CollectState.COLLECTION.getValue()) {//有记录且已收藏
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您已经收藏了该影片！");
            } else {//有记录且已取消收藏
                fm.setState(CollectState.COLLECTION.getValue());
                fm.setOperationTime(new Date());
                baseRepository.updateObj(fm);
                //用户收藏+1
                correlativeService.addCount(UserCorrelative.COLLECTS, fkUserId);
                return;
            }
        }
        FilmCollection filmCollection = new FilmCollection();
        filmCollection.setId(UUID.randomUUID().toString());
        filmCollection.setFkUserId(fkUserId);
        filmCollection.setFkFilmId(fkFilmId);
        filmCollection.setState(CollectState.COLLECTION.getValue());
        filmCollection.setOperationTime(new Date());//收藏时间

        //调用抠电影获取电影名字
        MovieVO movieVO = kouDianYingService.queryMovieById(fkFilmId);
        filmCollection.setFilmName(movieVO.getMovieName());

        baseRepository.saveObj(filmCollection);
        //用户收藏+1
        correlativeService.addCount(UserCorrelative.COLLECTS, fkUserId);

    }


    @Override
    @Transactional
    public void cancelFilmCollection(String fkUserId, String fkFilmId) {
        if (StringUtils.isBlank(fkUserId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "fkFilmId不能为空！");
        }
        if (StringUtils.isBlank(fkFilmId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "fkUserId不能为空！");
        }
        FilmCollection filmCollection = checkFilmIsCollection(fkUserId, fkFilmId);
        if (filmCollection != null && filmCollection.getState() != CollectState.CANCEL_COLLECT.getValue()) {
            filmCollection.setState(CollectState.CANCEL_COLLECT.getValue());
            filmCollection.setOperationTime(new Date());//取消收藏时间
            baseRepository.updateObj(filmCollection);
            //用户收藏-1
            correlativeService.reduceCount(UserCorrelative.COLLECTS, fkUserId);
        }
    }

    @Override
    public FilmCollection checkFilmIsCollection(String fkUserId, String fkFilmId) {
        if (StringUtils.isBlank(fkFilmId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "fkFilmId不能为空！");
        }
        if (StringUtils.isBlank(fkUserId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "fkUserId不能为空！");
        }
        String hql = baseRepository.getBaseHqlByClass(FilmCollection.class);
        hql += " and fkUserId ='" + fkUserId + "'";
        hql += " and fkFilmId ='" + fkFilmId + "'";
        FilmCollection filmCollection = baseRepository.queryUniqueData(hql);

        return filmCollection;
    }

    @Override
    public CutPage<FilmCollection> queryMyCollection(String fkUserId, Integer page, Integer limit) throws Exception {
        if (fkUserId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "fkUserId不能为空！");
        }
        String hql = "from FilmCollection where delFlag = 0 and fkUserId = :userId and state = 1";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",fkUserId);
        if (page != null && limit != null) {
            CutPage<FilmCollection> cutPage = baseRepository.executeHql(hql, map, page, limit);
            List<FilmCollection> newFilmList = new ArrayList<>();
            for (FilmCollection filmCollection : cutPage.getiData()) {
                MovieVO movie = kouDianYingService.queryMovieById(filmCollection.getFkFilmId());
                filmCollection.setMovie(movie);
                if (movie != null){
                    newFilmList.add(filmCollection);
                }
            }
            cutPage.setiData(newFilmList);
            return cutPage;
        } else {
            return baseRepository.executeHql(hql, map, 0, Integer.MAX_VALUE);
        }
    }

    @Override
    public void addFilmNameInDataBase() throws Exception {
        String hql = " from FilmCollection";
        CutPage<FilmCollection> filmCollectionCutPage = baseRepository.executeHql(hql, null, 0, Integer.MAX_VALUE);
        for (FilmCollection f : filmCollectionCutPage.getiData()) {
            String fkFilmId = f.getFkFilmId();
            //调用抠电影获取电影名字
            MovieVO movieVO = kouDianYingService.queryMovieById(fkFilmId);
            if (movieVO != null) {
                f.setFilmName(movieVO.getMovieName());
                baseRepository.updateObj(f);
            }
        }

    }

}
