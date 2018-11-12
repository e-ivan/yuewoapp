package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.FilmTopic;
import com.war4.pojo.FilmTopicComment;
import com.war4.pojo.UserCorrelative;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.vo.PushMsgVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/1/4.
 */
@Service
public class FilmTopicServiceImpl implements FilmTopicService,ApplicationContextAware {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private KouDianYingService kouDianYingService;

    @Autowired
    private UserCorrelativeService correlativeService;

    private ApplicationContext ac;

    @Override
    @Transactional
    public void addFilmTopic(FilmTopic filmTopic) {

        if(filmTopic.getFkUserId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkUserId不能为空！");
        }
        if(filmTopic.getFkFilmId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkFilmId不能为空！");
        }
        if(filmTopic.getContent()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"话题内容不能为空！");
        }
        FilmTopic ft = new FilmTopic();
        ft.setContent(filmTopic.getContent());
        ft.setFkFilmId(filmTopic.getFkFilmId());
        ft.setFkUserId(filmTopic.getFkUserId());
        ft.setTitle(filmTopic.getTitle());
        ft.setId(UUID.randomUUID().toString());
        baseRepository.saveObj(ft);
        //用户话题+1
        correlativeService.addCount(UserCorrelative.FILM_TOPICS,filmTopic.getFkUserId());

    }


    @Override
    @Transactional
    public void deleteFilmTopic(String topicId, String fkUserId) {
        FilmTopic filmTopic =  queryFilmTopicByIdAndUId(topicId,fkUserId);
        if(filmTopic==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"删除失败！");
        }
        baseRepository.logicDelete(filmTopic);
        //用户话题-1
        correlativeService.reduceCount(UserCorrelative.FILM_TOPICS,fkUserId);
    }

    @Override
    public FilmTopic queryFilmTopicByIdAndUId(String filmTopicId, String fkUserId) {
        String hql = "from FilmTopic where delFlag = 0 and id = :filmTopicId and fkUserId = :fkUserId";
        Map<String,Object> params = new HashMap<>();
        params.put("filmTopicId",filmTopicId);
        params.put("fkUserId",fkUserId);
        FilmTopic filmTopic = baseRepository.executeHql(hql, params);
        return filmTopic;
    }

    @Override
    public FilmTopic queryFilmTopicById(String topicId) {
        FilmTopic filmTopic = baseRepository.getObjById(FilmTopic.class,topicId);
        if(filmTopic!=null){
            filmTopic = assembleService.assembleObject(filmTopic);
        }
        return filmTopic;
    }

    @Override
    public CutPage<FilmTopic> queryAllFilmTopic(String orderBy, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(FilmTopic.class);
        if (StringUtils.isNotBlank(orderBy)){
            if ("comment".equals(orderBy.toLowerCase())) {
                hql += " order by commentCount desc";
            }else {
                hql += " order by createTime desc";
            }
        }else {
                hql += " order by createTime desc";
        }
        CutPage<FilmTopic> filmTopicCutPage = baseRepository.queryCutPageData(hql, new CutPage<FilmTopic>(page, limit));
        for (FilmTopic filmTopic : filmTopicCutPage.getiData()) {
            assembleService.assembleObject(filmTopic);
            try {
                filmTopic.setMovie(kouDianYingService.queryMovieById(filmTopic.getFkFilmId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filmTopicCutPage;
    }

    @Override
    public CutPage<FilmTopic> queryFilmTopics(String filmId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(FilmTopic.class);
        hql += " and fkFilmId ='"+filmId+"'";
        hql += " order by commentCount desc";
        CutPage<FilmTopic> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for (FilmTopic filmTopic:cutPage.getiData()){
            assembleService.assembleObject(filmTopic);
        }
        return cutPage;
    }

    @Override
    public CutPage<FilmTopic> queryFilmTopicsForUser(String fkUserId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(FilmTopic.class);
        hql += " and fkUserId ='"+fkUserId+"'";
        CutPage<FilmTopic> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for (FilmTopic filmTopic:cutPage.getiData()){
            assembleService.assembleObject(filmTopic);
        }
        return cutPage;
    }

    @Override
    @Transactional
    public void addFilmTopicComment(FilmTopicComment comment) {
        FilmTopic filmTopic = baseRepository.getObjById(FilmTopic.class, comment.getFkTopicId());
        if(StringUtils.isBlank(comment.getFkUserId())){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkUserId不能为空！");
        }
        if(filmTopic == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"电影话题不存在！");
        }
        if(StringUtils.isBlank(comment.getContent())){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"话题内容不能为空！");
        }
        comment.setId(UUID.randomUUID().toString());
        baseRepository.saveObj(comment);

        //电影话题增加一次评论
        filmTopic.setCommentCount(filmTopic.getCommentCount() + 1);
        baseRepository.updateObj(filmTopic);

        if(StringUtils.isNotBlank(comment.getReturnContentId())){
            FilmTopicComment returnComment = baseRepository.getObjById(FilmTopicComment.class,comment.getReturnContentId());
            if (returnComment == null){
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"回复的评论不存在！");
            }
            if (returnComment.getFkUserId().equals(comment.getFkUserId())){
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不能自言自语！");
            }
            //推送消息
            PushMsgVO pmv = new PushMsgVO(returnComment.getFkUserId(),comment.getFkUserId(),"有人回复了你的话题！",
                    comment.getContent(),comment.getId(),MessageLogType.FILM_TOPIC_COMMENT,true);
            ac.publishEvent(new PushEvent(pmv));//发送推送事件

        }
        //评论人不是发表人则推送
        if (!filmTopic.getFkUserId().equals(comment.getFkUserId())){
            PushMsgVO pmv = new PushMsgVO(filmTopic.getFkUserId(),comment.getFkUserId(),"有人评论了你的话题！",
                    comment.getContent(),comment.getId(),MessageLogType.FILM_TOPIC_COMMENT,true);
            ac.publishEvent(new PushEvent(pmv));//推送事件
        }
    }

    @Override
    @Transactional
    public void deleteFilmTopicComment(String filmTopicCommentId, String userId) {
        String hql = "from FilmTopicComment where id = :filmTopicCommentId and fkUserId = :userId";
        Map<String,Object> params = new HashMap<>();
        params.put("filmTopicCommentId",filmTopicCommentId);
        params.put("userId",userId);
        FilmTopicComment ftc = baseRepository.executeHql(hql, params);
        if (ftc == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"评论不存在！");
        }
        baseRepository.logicDelete(ftc);
        //话题评论-1
        hql = "update FilmTopic set commentCount = commentCount - 1 where id = '" + ftc.getFkTopicId() + "'";
        baseRepository.executeHql(hql,null);
    }

    @Override
    public CutPage<FilmTopicComment> queryFilmTopicCommtents(String topicId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(FilmTopicComment.class);
        hql += " and fkTopicId ='"+topicId+"' ";
        hql += " order by createTime desc ";
        CutPage<FilmTopicComment> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(FilmTopicComment filmTopicComment:cutPage.getiData()){
            assembleService.assembleObject(filmTopicComment);
        }
        return cutPage;
    }

    @Override
    public CutPage<FilmTopicComment> queryFilmTopicReturnCommtents(String returnCommentId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(FilmTopicComment.class);
        hql += " and returnContentId ='"+returnCommentId+"' ";
        hql += " order by createTime desc ";
        CutPage<FilmTopicComment> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(FilmTopicComment filmTopicComment:cutPage.getiData()){
            assembleService.assembleObject(filmTopicComment);
        }
        return cutPage;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
