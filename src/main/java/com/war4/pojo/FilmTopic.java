package com.war4.pojo;

import com.war4.vo.film.MovieVO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * 电影话题
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "film_topic")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class FilmTopic {

    @Id
    private String id;
    @Column(name = "fk_user_id")
    private String fkUserId;
    @Column(name = "fk_film_id")
    private String fkFilmId;

    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Transient
    private UserInfoBase user;

    @Transient
    private MovieVO movie;

    @Transient
    private List<FileUpload> photoList;


    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
