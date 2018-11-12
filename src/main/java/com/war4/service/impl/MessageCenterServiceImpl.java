package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.SystemParameters;
import com.war4.enums.ChatFriendType;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.MessageFriendApplyStatus;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.MessageFriendApply;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.ChatFriendService;
import com.war4.service.MessageCenterService;
import com.war4.vo.PushMsgVO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by dly on 2016/9/28.
 */
@Service
public class MessageCenterServiceImpl implements MessageCenterService, ApplicationContextAware {

    public Logger logger = Logger.getLogger(FilmRoomServiceImpl.class.getName());

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private ChatFriendService chatFriendService;

    @Autowired
    private AssembleService assembleService;

    private ApplicationContext ac;

    @Override
    @Transactional
    //好友申请---发送
    public String sendMakeFriendApply(String fromUserId, String toUserId, String content,boolean isPushMsg) {
        if (fromUserId.equals(toUserId)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不能添加自己为好友！");
        }
        MessageFriendApply messageFriendApply = new MessageFriendApply();
        String id = UUID.randomUUID().toString();
        messageFriendApply.setId(id);
        messageFriendApply.setFkFromUserId(fromUserId);
        messageFriendApply.setFkToUserId(toUserId);
        messageFriendApply.setMessageContent(content);
        messageFriendApply.setStatus(MessageFriendApplyStatus.CREATE.getCode());
        baseRepository.saveObj(messageFriendApply);

        if (isPushMsg){
            //推送消息
            PushMsgVO pmv = new PushMsgVO(toUserId, fromUserId, SystemParameters.MESSAGE_CHAT_ADD,
                    fromUserId + " 向 " + toUserId + " 申请好友", messageFriendApply.getId(),MessageLogType.FRIEND_APPLY,true);
            ac.publishEvent(new PushEvent(pmv));
        }

        return id;
    }

    @Override
    @Transactional
    //好友申请---处理
    public void resolveMakeFriendApply(String makeFriendApplyId, Integer result) {
        MessageFriendApply messageFriendApply = baseRepository.getObjById(MessageFriendApply.class, makeFriendApplyId);
        if (MessageFriendApplyStatus.getByCode(result) == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "状态不存在");
        }
        if (!messageFriendApply.getStatus().equals(MessageFriendApplyStatus.CREATE.getCode())) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "已操作");
        }
        if (result.equals(MessageFriendApplyStatus.ACCEPT.getCode())) {
            //申请人添加好友
            chatFriendService.addChatFriend(messageFriendApply.getFkFromUserId(), ChatFriendType.FRIEND.getCode(), messageFriendApply.getFkToUserId(), messageFriendApply.getId());
        }
        messageFriendApply.setStatus(result);
        baseRepository.updateObj(messageFriendApply);
    }

    @Override
    @Transactional
    public void deleteMessageApply(String applyId) {
        MessageFriendApply messageFriendApply = baseRepository.getObjById(MessageFriendApply.class, applyId);
        if (messageFriendApply != null) {
            baseRepository.logicDelete(messageFriendApply);
        }
    }

    @Override
    //好友申请---列表查询
    public CutPage<MessageFriendApply> queryMakeFriendApplyList(String myUserBaseId, Integer page, Integer limit) {
        CutPage<MessageFriendApply> cutPage = new CutPage<>(page, limit);
        String hql = baseRepository.getBaseHqlByClass(MessageFriendApply.class);
        hql += "and fkToUserId='" + myUserBaseId + "' ORDER BY createTime DESC";
        cutPage = baseRepository.queryCutPageData(hql, cutPage);
        for (MessageFriendApply messageFriendApply : cutPage.getiData()) {
            assembleService.assembleObject(messageFriendApply);
        }
        return cutPage;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    @Override
    @Transactional
    public void addBecomeFriends(String userId, String friendId){

        MessageFriendApply messageFriendApply = new MessageFriendApply();

        String id = UUID.randomUUID().toString();
        messageFriendApply.setId(id);
        messageFriendApply.setFkFromUserId(userId);
        messageFriendApply.setFkToUserId(friendId);
        messageFriendApply.setMessageContent("");
        messageFriendApply.setStatus(MessageFriendApplyStatus.ACCEPT.getCode());
        baseRepository.saveObj(messageFriendApply);

        chatFriendService.addChatFriend(messageFriendApply.getFkFromUserId(), ChatFriendType.FRIEND.getCode(), messageFriendApply.getFkToUserId(), messageFriendApply.getId());
    }

}
