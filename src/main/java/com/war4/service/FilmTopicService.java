package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.FilmTopic;
import com.war4.pojo.FilmTopicComment;

/**
 * Created by Administrator on 2017/1/4.
 */
public interface FilmTopicService {
    void addFilmTopic(FilmTopic filmTopic);
    void deleteFilmTopic(String topicId, String fkUserId);

    /**
     * 根据话题和用户获取话题
     * @param filmTopicId
     * @param fkUserId
     * @return
     */
    FilmTopic queryFilmTopicByIdAndUId(String filmTopicId,String fkUserId);
    FilmTopic queryFilmTopicById(String topicId);

    /**
     * 查询所有电影话题
     *@param orderBy
     */
    CutPage<FilmTopic> queryAllFilmTopic(String orderBy,Integer page,Integer limit);
    CutPage<FilmTopic> queryFilmTopics(String filmId,Integer page,Integer limit);
    CutPage<FilmTopic> queryFilmTopicsForUser(String fkUserId,Integer page,Integer limit);
    void addFilmTopicComment(FilmTopicComment comment);
    void deleteFilmTopicComment(String filmTopicCommentId,String userId);
    CutPage<FilmTopicComment> queryFilmTopicCommtents(String topicId,Integer page,Integer limit);
    CutPage<FilmTopicComment> queryFilmTopicReturnCommtents(String returnCommentId,Integer page,Integer limit);
}
