package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.ChatFriend;
import com.war4.pojo.UserInfoBase;

import java.util.Map;

/**
 * Created by Administrator on 2016/10/24.
 */
public interface ChatFriendService {
    void addChatFriend(String fkUserId,Integer type,String fkFriendUserId,String fkApplyId);
    ChatFriend queryFriendDetailById(String chatFriendId);
    ChatFriend queryFriendDetailForUser(String fkUserId,String userId);
    UserInfoBase queryIsFriendCheck(UserInfoBase userInfoBase,String userId);
    CutPage<ChatFriend> queryFriendsList(String userId,String note,Integer type, Integer page, Integer limit);
    void deleteChatFriend(String applyId);
    void updateChatFriend(String chatFriendId,Integer type);
    void updateChatFriendNote(String chatFriendId,String note);

}
