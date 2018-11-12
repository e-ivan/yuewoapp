package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户消息日志
 * Created by hh on 2017.7.12 0012.
 */
@Entity
@Table(name = "message_log")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MessageLog {
    public static final int SEND_SUCCESS = 0;    //已发送
    public static final int SEND_RECEIVE = 1;    //已接收
    public static final int SEND_FAIL = 2;       //发送失败

    public static final String PUSH_NOTIFICATION = "notification";  //通知
    public static final String PUSH_MESSAGE = "message";            //自定义消息


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;            //标题

    private String content;         //内容

    private String acceptUserId;    //接收人id

    private String sendUserId;      //发送人id

    private Date sendTime;          //发送时间

    private Date acceptTime;        //接收时间

    private Integer sendState;      //发送状态

    private Integer type;           //类型

    private String pushType;        //推送类型

    private String objectId;
}
