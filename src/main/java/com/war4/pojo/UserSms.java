package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户短信
 * Created by hh on 2017.8.15 0015.
 */
@Entity
@Table(name = "user_sms")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserSms {
    public static final int FAILED = 0;     //失败
    public static final int SEUCCESS = 1;   //成功
    public static final int SEND = 2;       //已发送
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mobile;
    private String userId;
    private String content; //内容
    private Date created;
    private Date sendTime;  //最后发送时间
    private int state;      //发送状态
    private String objId;   //关联对象
    private int sendCount;  //发送次数
    private int type;       //类型
}
