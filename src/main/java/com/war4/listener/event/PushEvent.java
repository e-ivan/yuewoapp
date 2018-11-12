package com.war4.listener.event;

import com.war4.vo.PushMsgVO;
import org.springframework.context.ApplicationEvent;

/**
 * 推送事件
 * Created by hh on 2017.7.19 0019.
 */
public class PushEvent extends ApplicationEvent {

    private PushMsgVO[] pushMsgVO;

    public PushEvent(PushMsgVO... pushMsgVO) {
        super(pushMsgVO);
        this.pushMsgVO = pushMsgVO;
    }

    public PushMsgVO[] getPushMsgVO() {
        return pushMsgVO;
    }
}
