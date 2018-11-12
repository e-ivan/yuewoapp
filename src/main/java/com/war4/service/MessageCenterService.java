package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.MessageFriendApply;

/**
 * Created by dly on 2016/9/28.
 */
public interface MessageCenterService {
    //好友申请---发送
    String  sendMakeFriendApply(String fromUserId,String toUserId,String content,boolean isPushMsg);

    //好友申请---处理
    void resolveMakeFriendApply(String makeFriendApplyId,Integer result);

    void deleteMessageApply(String applyId);

    //好友申请---列表查询
    CutPage<MessageFriendApply> queryMakeFriendApplyList(String myUserBaseId,Integer page,Integer limit);

    //两人变成好友
    void addBecomeFriends(String userId, String friendId);
}
