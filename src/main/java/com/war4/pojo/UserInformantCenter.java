package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 用户举报中心
 */
@Entity
@Table(name = "user_informant_center")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserInformantCenter {
    public static final int CREATE = 0; //创建
    public static final int PROCESS = 1; //处理中
    public static final int FINISH = 2; //处理完毕

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String informer;

    private String defendant;

    private String email;

    private String qq;

    private String phone;

    private int type;

    private int state;

    private Timestamp created;

    private Date operateTime;

    private String operatorId;

    private String content;

    private String operateContent;

}