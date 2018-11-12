package com.war4.service;

import com.war4.vo.PushMsgVO;

/**
 * 消息日志服务
 * Created by hh on 2017.7.12 0012.
 */
public interface MessageLogService {
    /**
     * 添加自定义消息日志
     * @param pushMsg
     */
    void addMessageLog(PushMsgVO pushMsg,Integer sendState);

}
