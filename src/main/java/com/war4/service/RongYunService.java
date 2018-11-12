package com.war4.service;

import com.war4.util.RongYunMessage.BaseMessage;

/**
 * Created by Administrator on 2016/11/4.
 */
public interface RongYunService {
    String getUserToken(String userId,String name,String portraitUri) throws Exception;
    void create(String[] userId, String groupId, String groupName) throws Exception;
    void join(String[] userId, String groupId, String groupName) throws Exception;
    void quit(String[] userId, String groupId) throws Exception;
    void createChatRoom(String id,String chatRoomName) throws Exception;
    void joinChatRoom(String userId,String chatroomId) throws Exception;
    void destroyChatRoom(String chatroomId) throws Exception;
    Integer queryChatRoom(String chatroomId) throws Exception;
    void publishChatroom(String fromUserId, String toChatroomId, BaseMessage message) throws Exception;
}
