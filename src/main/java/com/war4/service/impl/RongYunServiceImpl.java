package com.war4.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.service.RongYunService;
import com.war4.util.RequestUtil;
import com.war4.util.RongYunMessage.BaseMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/4.
 */
@Service
public class RongYunServiceImpl implements RongYunService {

    @Override
    public String getUserToken(String userId, String name, String portraitUri) throws Exception {
        if (userId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"请传入用户Id");
        }
        if (name == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"请传入用户昵称");
        }
        if (portraitUri == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"请传入用户头像");
        }
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("userId",userId);
        paramMap.put("name",name);
        paramMap.put("portraitUri",portraitUri);
        return RequestUtil.baseRongYunRequest(paramMap,"/user/getToken").getString("token");
    }

    @Override
    public void create(String[] userId, String groupId, String groupName) throws Exception {
        if (userId == null) {
            throw new IllegalArgumentException("Paramer 'userId' is required");
        }

        if (groupId == null) {
            throw new IllegalArgumentException("Paramer 'groupId' is required");
        }

        if (groupName == null) {
            throw new IllegalArgumentException("Paramer 'groupName' is required");
        }
        IdentityHashMap map = new IdentityHashMap();

        for (String child : userId) {
            map.put(new String("userId"), child);
        }
        map.put("groupId",groupId);
        map.put("groupName",groupName);
        RequestUtil.baseRongYunRequest(map, "/group/create");
    }


    /**
     * 退出群组方法（将用户从群中移除，不再接收该群组的消息.）
     *
     * @param  userId:要退出群的用户 Id.（必传）
     * @param  groupId:要退出的群 Id.（必传）
     *
     * @return CodeSuccessReslut
     **/
    @Override
    public void quit(String[] userId, String groupId) throws Exception {
        if (userId == null) {
            throw new IllegalArgumentException("Paramer 'userId' is required");
        }

        if (groupId == null) {
            throw new IllegalArgumentException("Paramer 'groupId' is required");
        }

        IdentityHashMap map = new IdentityHashMap();
        for (String child : userId) {
            map.put(new String("userId"), child);
        }
        map.put("groupId",groupId);
        RequestUtil.baseRongYunRequest(map, "/group/quit");
    }

    @Override
    public void createChatRoom(String id, String chatRoomName) throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("chatroom["+id+"]",chatRoomName);
        RequestUtil.baseRongYunRequest(map, "/chatroom/create");
    }

    @Override
    public void joinChatRoom(String userId, String chatroomId) throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("userId",userId);
        map.put("chatroomId",chatroomId);
        RequestUtil.baseRongYunRequest(map, "/chatroom/join");
    }

    @Override
    public void destroyChatRoom(String chatroomId) throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("chatroomId",chatroomId);
        RequestUtil.baseRongYunRequest(map, "/chatroom/destroy");
    }

    @Override
    public Integer queryChatRoom(String chatroomId) throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("chatroomId",chatroomId);
        map.put("count",String.valueOf(Integer.MAX_VALUE));
        map.put("order","1");
        JSONObject jsonObject = RequestUtil.baseRongYunRequest(map, "/chatroom/user/query");
        return jsonObject.getInteger("total");
    }

    /**
     * 将用户加入指定群组，用户将可以收到该群的消息，同一用户最多可加入 500 个群，每个群最大至 3000 人。
     *
     * @param  userId:要加入群的用户 Id，可提交多个，最多不超过 1000 个。（必传）
     * @param  groupId:要加入的群 Id。（必传）
     * @param  groupName:要加入的群 Id 对应的名称。（必传）
     *
     * @return CodeSuccessReslut
     **/

    @Override
    public void join(String[] userId, String groupId, String groupName) throws Exception {
        if (userId == null) {
            throw new IllegalArgumentException("Paramer 'userId' is required");
        }
        if (groupId == null) {
            throw new IllegalArgumentException("Paramer 'groupId' is required");
        }
        if (groupName == null) {
            throw new IllegalArgumentException("Paramer 'groupName' is required");
        }
        Map<String,String> map = new IdentityHashMap<>();

        for (String child : userId) {
            map.put(new String("userId"), child);
        }
        map.put("groupId",groupId);
        map.put("groupName",groupName);
        RequestUtil.baseRongYunRequest(map, "/group/join");
    }

    @Override
    public void publishChatroom(String fromUserId, String toChatroomId, BaseMessage message) throws Exception {
        if (fromUserId == null) {
            throw new IllegalArgumentException("Paramer 'fromUserId' is required");
        }

        if (toChatroomId == null) {
            throw new IllegalArgumentException("Paramer 'toChatroomId' is required");
        }

        if (message == null) {
            throw new IllegalArgumentException("Paramer 'message' is required");
        }
        Map<String,String> map = new HashMap<>();
        map.put("fromUserId",fromUserId);
        map.put("toChatroomId",toChatroomId);
        map.put("objectName",message.getType());
        map.put("content",message.toString());
        RequestUtil.baseRongYunRequest(map, "/message/chatroom/publish");
    }
}
