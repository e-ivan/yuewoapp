package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 活动
 * Created by Administrator on 2016/12/7.
 */

@Entity
@Table(name = "activity")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter@Setter
public class Activity {
    @Id
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "activity_url")
    private String activityUrl;

    @Column(name = "type")
    private Integer type;

    @Transient
    private String photoUrl;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

    @Column(name = "intro")
    private String intro;              //简介

    @Column(name = "out_url")
    private String outUrl;              //外部链接

    @Column(name = "detail")
    private String detail;              //详情

    @Column(name = "start_time")
    private Timestamp startTime;              //活动开始时间

    @Column(name = "end_time")
    private Timestamp endTime;              //活动结束时间

    @Transient
    private Integer endFlag;              //活动结束标记

}
