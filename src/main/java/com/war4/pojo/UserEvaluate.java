package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 约会后对人的评价
 * Created by Administrator on 2017/4/6.
 */
@Entity
@Table(name = "user_evaluate")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserEvaluate {
    @Id
    private String id;
    //评价人Id
    @Column(name = "fk_user_id")
    private String userId;
    //被评价人Id
    @Column(name = "evaluate_id")
    private String appraiser;
    @Column(name = "content")
    private String content;
    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

    @Column(name = "fenshu")
    private Float fenShu;
    @Transient
    UserInfoBase userInfoBase;
    @Transient
    UserInfoBase appraiserInfoBase;
}
