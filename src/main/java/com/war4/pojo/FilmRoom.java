package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 开房间
 * Created by Administrator on 2016/12/30.
 */
@Entity
@Table(name = "film_room")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class FilmRoom {
    @Id
    private String id;

    @Column(name = "fk_user_id")
    private String fkUserId;

    @Column(name = "cid")
    private String cid;
    @Column(name = "pid")
    private String pid;

    @Column(name = "fk_film_id")
    private String fkFilmId;

    @Column(name = "name")
    private String name;
    @Column(name = "status")
    private Integer status;
    @Column(name = "play_time")
    private String playTime;
    @Column(name = "count")
    private Integer count;
    @Column(name = "room_tpye")
    private String roomTpye;
    @Column(name = "room_intro")
    private String roomIntro;
    @Column(name = "total_money")
    private BigDecimal totalMoney;

    @Column(name = "room_password")
    private String roomPassword;
    @Column(name = "push_url")
    private String pushUrl;
    @Column(name = "http_pull_url")
    private String httpPullUrl;
    @Column(name = "hls_pull_url")
    private String hlsPullUrl;
    @Column(name = "rtmp_pull_url")
    private String rtmpPullUrl;

    @Transient
    private Film film;

    @Transient
    private Integer roomPerson;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
