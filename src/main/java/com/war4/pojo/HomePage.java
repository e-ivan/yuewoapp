package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 首页轮播图
 * Created by Administrator on 2016/12/7.
 */
@Entity
@Table(name = "home_page")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class HomePage {
    @Id
    private String id;

    @Column(name = "url")
    private String url;

    @Transient
    private String photoUrl;

    @Column(name = "title")
    private String title;

    @Column(name = "movie_id")
    private String movieId;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                    //删除标记

    @Column(name = "content")
    private String content;         //分享内容

    @Column(name = "type")
    private Integer type;         //弹窗还是轮播，还是两者同时

    @Transient
    private String sharePicUrl;

    @Transient
    private String popUpPicUrl;

    @Column(name = "activity_id")
    private String activityId;              //活动id

    @Column(name = "location")
    private Integer location;              // 定位。1：网页  2：app内部  3：报名表

    @Transient
    private Activity activity;  //活动
}
