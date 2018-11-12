package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.ChatFriendType;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.ChatFriend;
import com.war4.pojo.UserBlack;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.ChatFriendService;
import com.war4.service.UserBlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2017/1/5.
 */
@Service
public class UserBlackServiceImpl implements UserBlackService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;
    @Autowired
    private ChatFriendService chatFriendService;
    @Override
    @Transactional
    public void addUserBlack(String fkUserId, String blackUserId) {
        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkUserId不能为空！");
        }
        if(blackUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"blackUserId不能为空！");
        }
        if(findUserBlack(fkUserId, blackUserId)!=null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"TA已经在您的黑名单里了！");
        }
        UserBlack userBlack = new UserBlack();
        userBlack.setId(UUID.randomUUID().toString());
        userBlack.setBlackUserId(blackUserId);
        userBlack.setFkUserId(fkUserId);
        baseRepository.saveObj(userBlack);
        ChatFriend chatFriend=chatFriendService.queryFriendDetailForUser(blackUserId,fkUserId);
        //chatFriend.setType(ChatFriendType.USERBLACK.getCode());
        chatFriendService.updateChatFriend(chatFriend.getId(),ChatFriendType.USERBLACK.getCode());
    }

    @Override
    @Transactional
    public void removeUserBlack(String fkUserId, String blackUserId) {
        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkUserId不能为空！");
        }
        if(blackUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"blackUserId不能为空！");
        }
        UserBlack userBlack =  findUserBlack(fkUserId, blackUserId);

        if(userBlack!=null){
            baseRepository.logicDelete(userBlack);
            ChatFriend chatFriend=chatFriendService.queryFriendDetailForUser(blackUserId,fkUserId);
            //chatFriend.setType(ChatFriendType.USERBLACK.getCode());
            chatFriendService.updateChatFriend(chatFriend.getId(),ChatFriendType.FRIEND.getCode());
        }
    }

    @Override
    public UserBlack findUserBlack(String fkUserId,String blackUserId){
        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkUserId不能为空！");
        }
        if(blackUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"blackUserId不能为空！");
        }
        String hql = baseRepository.getBaseHqlByClass(UserBlack.class);
        hql += " and fkUserId ='"+fkUserId+"' ";
        hql += " and blackUserId ='"+blackUserId+"' ";
        UserBlack userBlack = baseRepository.queryUniqueData(hql);
        if(userBlack!=null){
            userBlack = assembleService.assembleObject(userBlack);
        }
        return userBlack;
    }

    @Override
    public CutPage<UserBlack> queryMyUserBlack(String fkUserId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(UserBlack.class);
        hql +=" and fkUserId = '"+fkUserId+"' ";
        CutPage<UserBlack> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(UserBlack userBlack:cutPage.getiData()){
            userBlack = assembleService.assembleObject(userBlack);
        }
        return cutPage;
    }
}
