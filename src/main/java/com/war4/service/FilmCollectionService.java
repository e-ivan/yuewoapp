package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.FilmCollection;

/**
 * Created by Administrator on 2017/1/4.
 */
public interface FilmCollectionService {
    void addFilmCollection(String fkUserId,String fkFilmId) throws Exception;
    void cancelFilmCollection(String fkUserId, String fkFilmId);
    FilmCollection checkFilmIsCollection(String fkUserId, String fkFilmId);
    CutPage<FilmCollection> queryMyCollection(String fkUserId,Integer page,Integer limit) throws Exception;
    void  addFilmNameInDataBase()throws Exception;

}
