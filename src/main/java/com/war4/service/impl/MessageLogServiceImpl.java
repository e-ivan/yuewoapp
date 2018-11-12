package com.war4.service.impl;

import com.war4.pojo.MessageLog;
import com.war4.repository.BaseRepository;
import com.war4.service.MessageLogService;
import com.war4.vo.PushMsgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by hh on 2017.7.12 0012.
 */
@Service
public class MessageLogServiceImpl implements MessageLogService{
    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public void addMessageLog(PushMsgVO pushMsg,Integer sendState) {
        MessageLog messageLog = new MessageLog();
        messageLog.setSendTime(new Date());
        messageLog.setTitle(pushMsg.getTitle());
        messageLog.setContent(pushMsg.getMsg());
        messageLog.setObjectId(pushMsg.getObjectId());
        messageLog.setPushType(pushMsg.isAlert() ? MessageLog.PUSH_NOTIFICATION : MessageLog.PUSH_MESSAGE);
        messageLog.setAcceptUserId(pushMsg.getAcceptUserId());
        messageLog.setSendUserId(pushMsg.getSendUserId());
        messageLog.setSendState(sendState);
        messageLog.setType(pushMsg.getMessageLogType().getCode());
        baseRepository.saveObj(messageLog);
        System.out.println("-----------------保存推送消息日志------------------");
    }
}
