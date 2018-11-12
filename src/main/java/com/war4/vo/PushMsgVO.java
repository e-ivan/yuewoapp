package com.war4.vo;

import com.war4.enums.MessageLogType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 推送消息的vo
 * Created by hh on 2017.7.19 0019.
 */
@Getter
@Setter
public class PushMsgVO {
    private String acceptUserId;    //接收人
    private String sendUserId;      //发送人
    private String title;           //标题
    private String msg;             //内容
    private String objectId;        //对象id
    private MessageLogType messageLogType; //信息类型
    private boolean alert;          //是否发送alert消息
    private Map<String,String> extras = new HashMap<>();//自定义消息内容

    public PushMsgVO(String acceptUserId, String sendUserId, String title, String msg, Object objectId,MessageLogType messageLogType, boolean alert) {
        this.acceptUserId = acceptUserId;
        this.sendUserId = sendUserId;
        this.title = title;
        this.msg = msg;
        this.objectId = objectId != null ? objectId.toString() : null;
        this.messageLogType = messageLogType;
        this.alert = alert;
    }

    public PushMsgVO(String acceptUserId, String sendUserId, String title, String msg, MessageLogType messageLogType, boolean alert) {
        this(acceptUserId,sendUserId,title,msg,null,messageLogType,alert);
    }

    public PushMsgVO(String acceptUserId, String sendUserId, String title, String msg, MessageLogType messageLogType) {
        this(acceptUserId,sendUserId,title,msg,null,messageLogType,false);
    }

    public PushMsgVO(String acceptUserId, String sendUserId, String title, String msg,Object objectId, MessageLogType messageLogType) {
        this(acceptUserId,sendUserId,title,msg,objectId,messageLogType,false);
    }

    public Map<String, String> getExtras() {
        //初始化
        this.extras.put("sendUserId",this.sendUserId == null ? "" : this.sendUserId);
        this.extras.put("acceptUserId",this.acceptUserId);
        this.extras.put("type", messageLogType.getValue());
        return extras;
    }
    public void addExtras(String name,String value){
        this.extras.put(name,value);
    }
}
