package com.war4.pojo;

import com.war4.vo.film.MovieVO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 电影评论
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "film_comment")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class FilmComment {

    @Id
    private String id;
    @Column(name = "fk_user_id")
    private String fkUserId;
    @Column(name = "fk_film_id")
    private String fkFilmId;

    @Column(name = "score")
    private BigDecimal score;
    @Column(name = "message")
    private String message;

    @Transient
    private UserInfoBase user;


    @Transient
    private MovieVO movie;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
