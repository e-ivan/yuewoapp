package com.war4.controller;

import com.war4.base.*;
import com.war4.enums.ChatFriendType;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.ChatFriend;
import com.war4.pojo.UserBlack;
import com.war4.pojo.UserInfoBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dly on 2016/8/8.
 */
@Controller
@RequestMapping(value = "/chat")
public class ChatController  extends BaseController {

    //查询好友个人信息
    @RequestMapping(value = "queryFriendInfo")
    public
    @ResponseBody
    Response queryFriendInfo(String friendUserId,String userId){

        UserInfoBase userInfoBase = userInfoBaseService.getUserById(friendUserId);
        if(userInfoBase==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "不存在该用户！");
        }
        userInfoBase = chatFriendService.queryIsFriendCheck(userInfoBase,userId);

        return new ObjectResponse<>(userInfoBase);
    }
    //删除好友个人信息
    @RequestMapping(value = "deleteChatFriend")
    public
    @ResponseBody
    Response deleteChatFriend(String applyId){
       chatFriendService.deleteChatFriend(applyId);
        return new Response("删除成功");
    }
    //修改备注
    @RequestMapping(value = "updateChatFriendNote")
    public
    @ResponseBody
    Response updateChatFriendNote(String chatFriendId, String note){
        chatFriendService.updateChatFriendNote(chatFriendId,note);
        return new Response("修改成功");
    }

    //查询好友列表
    @RequestMapping(value = "queryMyFriends")
    public
    @ResponseBody
    Response queryMyFriends(String userId,String note,Integer type, Integer page, Integer limit){
        CutPage<ChatFriend> cutPage = chatFriendService.queryFriendsList(userId,note,type, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //添加会话
    @RequestMapping(value = "addConversation")
    public
    @ResponseBody
    Response addConversation(String fkUserId, String fkFriendUserId){

        chatFriendService.addChatFriend(fkUserId, ChatFriendType.STRANGER.getCode(),fkFriendUserId,null);

        return new Response("添加成功");
    }

    //发送群消息
    @RequestMapping(value = "sendGroupChatMessage")
    public
    @ResponseBody
    Response sendGroupChat(){
        return null;
    }

    //发送单人聊天消息
    @RequestMapping(value = "sendSingleChatMessage")
    public
    @ResponseBody
    Response sendSingleChatMessage(){
        return null;
    }

    //加入黑名单
    @RequestMapping(value = "addUserBlack")
    public
    @ResponseBody
    Response addUserBlack(String fkUserId, String blackUserId){
        userBlackService.addUserBlack(fkUserId, blackUserId);
        return new Response("加入黑名单成功！");
    }

    //移除黑名单
    @RequestMapping(value = "removeUserBlack")
    public
    @ResponseBody
    Response removeUserBlack(String fkUserId, String blackUserId){
        userBlackService.removeUserBlack(fkUserId, blackUserId);
        return new Response("移除黑名单成功！");
    }

    //查看黑名单
    @RequestMapping(value = "findUserBlack")
    public
    @ResponseBody
    Response findUserBlack(String fkUserId, String blackUserId){
        UserBlack userBlack =  userBlackService.findUserBlack(fkUserId, blackUserId);
        return new ObjectResponse<>(userBlack);
    }

    //查看我的黑名单
    @RequestMapping(value = "queryMyUserBlack")
    public
    @ResponseBody
    Response queryMyUserBlack(String fkUserId, Integer page, Integer limit){
        CutPage cutPage =  userBlackService.queryMyUserBlack(fkUserId, page,limit);
        return new ObjectResponse<>(cutPage);
    }

    //把两人变成好友
    @RequestMapping(value = "addBecomeFriends")
    public
    @ResponseBody
    Response addBecomeFriends(String userId, String friendId){
        messageCenterService.addBecomeFriends(userId,friendId);
        return new ObjectResponse<>("已变成好友！");
    }

}
