package com.war4.pojo;

/**
 * Created by Administrator on 2017/4/18.
 */

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "trailer")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Trailer {
    @Id
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "start_time")
    private String startTime;
    @Column(name = "content")
    private String content;
    @Column(name = "title")
    private String title;
    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
    @Transient
    private String trailerPhoto;
    @Transient
    private UserInfoBase userInfoBase;
}
