package com.war4.pojo;

import com.war4.vo.film.MovieVO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 收藏电影
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "film_collection")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class FilmCollection {

    @Id
    private String id;
    @Column(name = "fk_user_id")
    private String fkUserId;
    @Column(name = "fk_film_id")
    private String fkFilmId;

    @Column(name = "film_name")
    private String filmName;

    @Column(name = "state")
    private int state;

    @Column(name = "operation_time")
    private Date operationTime;                         //操作时间

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

    @Transient
    private MovieVO movie;
}
