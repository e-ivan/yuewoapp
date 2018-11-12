package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.AnchorRoom;
import com.war4.pojo.Trailer;

import java.io.IOException;

/**
 * Created by Administrator on 2016/12/7.
 */
public interface AnchorRoomService {
    AnchorRoom addAnchorRoom(String userId,String name) throws IOException, Exception;
    void closeAnchorRoom(String roomId) throws Exception;
    void joinAnchorRoom(String roomId,String userId) throws Exception;
    void editAnchorRoom(String roomId,String userId) throws Exception;
    void updateAnchorRoom(String roomId,String name);
    Integer queryRoomPeopleCount(String roomId) throws Exception;
    AnchorRoom queryAnchorRoom(String roomId);
    AnchorRoom getAnchorRoomByUserId(String userId);
    void updateAnchorRoomPeopleTotal(AnchorRoom anchorRoom) throws Exception;
    void addAnchorUserGift(String roomId,String fkUserId, String giftId, Integer count) throws Exception;
    boolean checkUserHaveAnchor(String userId);
    CutPage<AnchorRoom> queryAnchorRooms(Integer type,Integer page,Integer limit);
    CutPage<AnchorRoom> queryMyAttentionAnchor(String userId,Integer role ,Integer page,Integer limit);
    CutPage<AnchorRoom> queryHostAnchor(Integer role,Integer page,Integer limit);
    CutPage<AnchorRoom> queryNearAnchor(String x,String y,Integer role ,Integer sex,Integer page,Integer limit);
    void operationRecording(String userId,String roomId,Integer needRecord,Integer format,Integer duration,String filename);
    Trailer addTrailer(String userId,String startTime,String title,String content);
    CutPage<Trailer> queryTrailerList(String userId,Integer page,Integer limit);
    Trailer queryTrailer(String id);
    AnchorRoom updateAnchorRoom(String id,Integer type);
    void setVoide(String cid,String userId);
    String findAnchorRoom(String roomId);

}
