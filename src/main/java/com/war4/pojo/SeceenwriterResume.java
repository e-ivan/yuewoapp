package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * 编剧简历
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "screenwriter_resume")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class SeceenwriterResume {

    @Id
    private String fkUserId;

    @Column(name = "name")
    private String name;
    @Column(name = "sex")
    private Integer sex;
    @Column(name = "age")
    private String age;
    @Column(name = "constellation")
    private String constellation;
    @Column(name = "height")
    private String height;
    @Column(name = "weight")
    private String weight;
    @Column(name = "city")
    private String city;
    @Column(name = "school")
    private String school;
    @Column(name = "major")
    private String major;


    @Column(name = "person_label")
    private String personLabel;
    @Column(name = "is_public")
    private Integer isPublic;


    @Transient
    private String coverPhoto;

    @Transient
    private Integer count;

    @Transient
    private List<FileUpload> photoWall;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
