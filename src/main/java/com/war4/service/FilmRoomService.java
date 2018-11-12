package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.AnchorRoom;
import com.war4.pojo.FilmRoom;
import com.war4.pojo.FilmRoomUser;

/**
 * Created by Administrator on 2016/12/30.
 */
public interface FilmRoomService {
    FilmRoom addFilmRoom(FilmRoom filmRoom) throws Exception;
    void closeFilmRoon(String roomId) throws Exception;
    void joinFilmRoom(String fkUserId,String fkRoomId);
    void updateFilmRoomPid(String roomId,String pid);
    FilmRoomUser queryFilmRoomUser(String fkUserId,String fkRoomId);
    void deleteFilmRoomUser(String fkUserId,String fkRoomId) throws Exception;
    void addAnchorUserGift(String roomId,String fkUserId, String giftId, Integer count) throws Exception;
    FilmRoom queryFilmRoom(String roomId);
    CutPage<FilmRoom> queryFileRoomList(String name, Integer page, Integer limit);
    CutPage<FilmRoomUser> queryFilmRoomUsers(String roomId);
    Integer queryFilmRoomUsersCount(String roomId);
    void updateFilmRoomStatus(String roomId) throws Exception;
}
