package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 剧组
 * Created by Administrator on 2016/12/22.
 */
@Entity
@Table(name = "crew")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Crew {
    @Id
    private String id;

    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Column(name = "genre")
    private String genre;
    @Column(name = "publish_platform")
    private String publishPlatform;

    @Column(name = "open_time")
    private String openTime;
    @Column(name = "start_recruit_time")
    private String startRecruitTime;
    @Column(name = "end_recruit_time")
    private String endRecruitTime;

    @Column(name = "shooting_location")
    private String shootingLocation;
    @Column(name = "shooting_cycle")
    private String shootingCycle;

    @Transient
    private String backPhoto;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
