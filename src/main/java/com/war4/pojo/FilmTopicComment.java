package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 电影话题评论
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "film_topic_comment")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class FilmTopicComment {
    @Id
    private String id;

    @Column(name = "fk_user_id")
    private String fkUserId;
    @Column(name = "fk_topic_id")
    private String fkTopicId;

    @Column(name = "return_content_id")
    private String returnContentId;

    @Column(name = "return_content")
    private String returnContent;

    @Column(name = "content")
    private String content;

    @Transient
    private UserInfoBase user;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
