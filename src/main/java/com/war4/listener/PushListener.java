package com.war4.listener;

import cn.jpush.api.push.PushResult;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.MessageLog;
import com.war4.pojo.UserInfoBase;
import com.war4.repository.BaseRepository;
import com.war4.service.MessageLogService;
import com.war4.service.PushPayloadService;
import com.war4.vo.PushMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 推送监听器
 * Created by hh on 2017.7.19 0019.
 */
@Component
@Slf4j
public class PushListener implements ApplicationListener<PushEvent> {

    @Autowired
    private PushPayloadService pushService;
    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private MessageLogService messageLogService;

        @Async
    @Override
    public void onApplicationEvent(PushEvent pushEvent) {

        try {
            PushMsgVO[] pmvs = pushEvent.getPushMsgVO();
            for (PushMsgVO pushMsgVO : pmvs) {
                Integer sendState = MessageLog.SEND_SUCCESS;
                PushResult pushResult;
                try {
                    UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, pushMsgVO.getAcceptUserId());
                    if (user.getDeviceToken() != null) {
                        pushResult = pushService.pushMessage2RegistrationId(pushMsgVO.getTitle(), pushMsgVO.getExtras(), pushMsgVO.isAlert(), user.getDeviceToken());
                    }else {
                        pushResult = pushService.pushMessage2Alias(pushMsgVO.getTitle(), pushMsgVO.getExtras(), pushMsgVO.isAlert(),user.getId());
                    }
                    if (pushResult.sendno <= 0) {
                        sendState = MessageLog.SEND_FAIL;
                        log.warn("推送失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendState = MessageLog.SEND_FAIL;
                    log.warn("推送失败 :" + e.getMessage());
                }finally {
                    messageLogService.addMessageLog(pushMsgVO, sendState);//保存自定义消息日志
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
