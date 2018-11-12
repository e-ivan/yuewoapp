package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.ChatFriendType;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.CommonWhether;
import com.war4.pojo.ChatFriend;
import com.war4.pojo.UserInfoBase;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.ChatFriendService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/10/24.
 */
@Service
public class ChatFriendServiceImpl implements ChatFriendService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public void addChatFriend(String fkUserId, Integer type,String fkFriendUserId, String fkApplyId) {

        if(ChatFriendType.getByValue(type)==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "好友类型不存在!");
        }
        if(fkUserId.equals(fkFriendUserId)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能添加自己为好友!");
        }
        ChatFriend chatFriendMe =  queryFriendDetailForUser(fkUserId,fkFriendUserId);
        if(chatFriendMe==null){
            addNewChatFriend(fkUserId, type, fkFriendUserId, fkApplyId);
        }
        ChatFriend chatFriendYou =  queryFriendDetailForUser(fkFriendUserId,fkUserId);
        if(chatFriendYou==null){
           addNewChatFriend(fkFriendUserId, type,fkUserId , fkApplyId);
        }
    }

    @Transactional
    public void addNewChatFriend(String fkUserId, Integer type,String fkFriendUserId, String fkApplyId){
        ChatFriend chatFriend  = new ChatFriend();
        chatFriend.setId(UUID.randomUUID().toString());
        chatFriend.setFkUserId(fkFriendUserId);
        chatFriend.setFkFriendUserId(fkUserId);
        if(fkApplyId!=null){
            chatFriend.setFkApplyId(fkApplyId);
        }
        chatFriend.setType(type);
        baseRepository.saveObj(chatFriend);
    }

    @Override
    @Transactional
    public ChatFriend queryFriendDetailById(String chatFriendId) {
        ChatFriend chatFriend = baseRepository.getObjById(ChatFriend.class,chatFriendId);
        chatFriend = assembleService.assembleObject(chatFriend);
        return chatFriend;
    }

    @Override
    @Transactional
    public ChatFriend queryFriendDetailForUser(String fkUserId,String userId) {
        String hql = baseRepository.getBaseHqlByClass(ChatFriend.class);
        hql +=" and fkUserId = '"+userId+"' and fkFriendUserId ='"+fkUserId+"' ";
        ChatFriend chatFriend = baseRepository.queryUniqueData(hql);
        return chatFriend;
    }

    @Override
    @Transactional
    public UserInfoBase queryIsFriendCheck(UserInfoBase userInfoBase, String userId) {
        if(userInfoBase!=null){
           ChatFriend chatFriend =  queryFriendDetailForUser(userInfoBase.getId(),userId);
            if(chatFriend!=null){
                userInfoBase.setChatFriend(chatFriend);
                userInfoBase.setIsFriend(CommonWhether.TRUE.getCode());
            }else{
                userInfoBase.setIsFriend(CommonWhether.FALSE.getCode());
            }
        }
        return userInfoBase;
    }

    @Override
    @Transactional
    public CutPage<ChatFriend> queryFriendsList(String userId,String note,Integer type, Integer page, Integer limit) {
        if(StringUtils.isBlank(userId)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空");
        }
        String hql = baseRepository.getBaseHqlByClass(ChatFriend.class);
        if(type!=null){
            if(ChatFriendType.getByValue(type)==null){
                throw new BusinessException(CommonErrorResult.SECRET_FAIL, "好友类型不存在!");
            }
            hql += " and type = "+type +"  ";
        }
        hql += " and type = "+1 +"  ";
        if(StringUtils.isNotBlank(note)){
            hql += " and note like '%"+note+"%' ";
        }
        hql +=" and fkUserId = '"+userId+"' ";

        CutPage<ChatFriend> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for (ChatFriend chatFriend : cutPage.getiData()){
            assembleService.assembleObject(chatFriend);
        }
        return cutPage;
    }

    @Override
    @Transactional
    public void deleteChatFriend(String applyId) {
        if (StringUtils.isBlank(applyId)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"申请id不能为空");
        }
        String hql = "update ChatFriend set delFlag = 1 where fkApplyId = :applyId";
        Map<String,Object> params = new HashMap<>();
        params.put("applyId",applyId);
        int i = baseRepository.executeHql(hql, params);
        if (i <= 0){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"删除好友失败！");
        }
    }

    @Override
    @Transactional
    public void updateChatFriend(String chatFriendId, Integer type) {
        ChatFriend chatFriend = queryFriendDetailById(chatFriendId);
        if (chatFriend ==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该好友不存在");
        }
        chatFriend.setType(type);
        baseRepository.updateObj(chatFriend);
    }

    @Override
    @Transactional
    public void updateChatFriendNote(String chatFriendId, String note) {
        ChatFriend chatFriend = queryFriendDetailById(chatFriendId);
        if (chatFriend ==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该好友不存在");
        }
        chatFriend.setNote(note);
        baseRepository.updateObj(chatFriend);
    }


}
