package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 简历作品详细
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "resume_works_item")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class ResumeWorItem {
    @Id
    private String id;

    @Column(name = "fk_user_id")
    private String fkUserId;
    @Column(name = "work_type")
    private String workType;
    @Column(name = "title")
    private String title;
    @Column(name = "intro")
    private String intro;

    @Transient
    private String coverPhoto;


    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
