package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户反馈
 * Created by hh on 2017.8.2 0002.
 */
@Entity
@Table(name = "user_feedback")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserFeedback {
    public static final int CREATE = 0; //创建
    public static final int PROCESS = 1; //处理中
    public static final int FINISH = 2; //处理完毕

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fb_id")
    private Long id;

    private String userId;  //用户id

    private String phone;   //手机

    private String qq;   //手机

    private String email;   //手机

    private String content; //内容

    private String operatorId;  //处理人id

    private String operateContent;//反馈回复内容

    private int state;  //状态

    private int type;   //类型

    private Date operateTime;   //处理时间

    private Date created;

}
