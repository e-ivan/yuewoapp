package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.FilmComment;
import com.war4.pojo.UserCorrelative;
import com.war4.pojo.UserInfoBase;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.FilmCommentService;
import com.war4.service.UserCorrelativeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2017/1/5.
 */
@Service
public class FilmCommentServiceImpl implements FilmCommentService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private UserCorrelativeService correlativeService;

    @Override
    @Transactional
    public void addFilmComment(FilmComment filmComment) {

        if(filmComment.getFkFilmId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkFilmId不能为空！");
        }
        if(filmComment.getFkUserId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkUserId不能为空！");
        }
        if(filmComment.getScore()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"评分不能为空！");
        }
        if (filmComment.getMessage()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"内容不能为空！");
        }

        if(queryFilmCommentByFilmIdAndUserId(filmComment.getFkUserId(),filmComment.getFkFilmId())!=null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"您已经发表过评论了！");
        }

        filmComment.setId(UUID.randomUUID().toString());
        baseRepository.saveObj(filmComment);
        //用户影评+1
        correlativeService.addCount(UserCorrelative.FILM_COMMENTS,filmComment.getFkUserId());
    }

    @Override
    @Transactional
    public void deleteFilmComment(String commentId) {
        FilmComment filmComment = baseRepository.getObjById(FilmComment.class,commentId);
        if(filmComment == null || filmComment.getDelFlag() == 1){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"影评不存在！");
        }
        baseRepository.logicDelete(filmComment);
        //用户影评-1
        correlativeService.reduceCount(UserCorrelative.FILM_COMMENTS,filmComment.getFkUserId());
    }

    @Override
    public FilmComment queryFilmCommentByFilmIdAndUserId(String fkUserId, String filmId) {
        String hql = baseRepository.getBaseHqlByClass(FilmComment.class);
        hql += " and fkFilmId = '"+filmId+"' ";
        hql += " and fkUserId = '"+fkUserId+"' ";
        FilmComment filmComment = baseRepository.queryUniqueData(hql);
        return filmComment;
    }

    @Override
    public CutPage<FilmComment> queryFilmComments(String filmId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(FilmComment.class);
        hql += " and fkFilmId = '"+filmId+"' ";
        hql += " order by createTime desc";
        CutPage<FilmComment> commentCutPage  = new CutPage<>(page, limit);
        commentCutPage = baseRepository.queryCutPageData(hql,commentCutPage);
        for (FilmComment filmComment : commentCutPage.getiData()) {
            filmComment.setUser(baseRepository.getObjById(UserInfoBase.class,filmComment.getFkUserId()));
        }
        return commentCutPage;
    }

    @Override
    public CutPage<FilmComment> queryMyFilmComment(String fkUserId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(FilmComment.class);
        hql += " and fkUserId = '"+fkUserId+"' ";
        hql += " order by createTime desc";
        CutPage<FilmComment> commentCutPage  = new CutPage<>(page, limit);
        commentCutPage = baseRepository.queryCutPageData(hql,commentCutPage);
        for(FilmComment filmComment:commentCutPage.getiData()){
            assembleService.assembleObject(filmComment);
        }
        return commentCutPage;

    }

}
