package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.FilmComment;

/**
 * Created by Administrator on 2017/1/5.
 */
public interface FilmCommentService {
    void addFilmComment(FilmComment filmComment);
    void deleteFilmComment(String commentId);
    FilmComment queryFilmCommentByFilmIdAndUserId(String fkUserId,String filmId);
    CutPage<FilmComment> queryFilmComments(String filmId,Integer page,Integer limit);
    CutPage<FilmComment> queryMyFilmComment(String fkUserId,Integer page,Integer limit);
}
