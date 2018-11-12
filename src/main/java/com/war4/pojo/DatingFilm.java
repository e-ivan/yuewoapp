package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 约会看电影
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "dating_film")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class DatingFilm {

    @Id
    private String id;

    @Column(name = "create_user_id")
    private String createUserId;

    @Column(name = "accept_user_id")
    private String acceptUserId;

    @Column(name = "fk_user_gift_id")
    private String fkUserGiftId;

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    private Integer status;

    @Column(name = "film_id")
    private String filmId;

    @Column(name = "intro")
    private String intro;

    @Column(name = "dating_type")
    private String datingType;

    @Column(name = "dating_time")
    private String datingTime;

    @Column(name = "dating_film")
    private String datingFilm;

    @Column(name = "dating_cinema")
    private String datingCinema;

    @Column(name = "transport")
    private String transport;

    @Column(name = "can_bring_friends")
    private Integer canBringFriends;

    @Column(name = "pay_type")
    private Integer payType;

    @Transient
    private UserGift userGift;

    @Transient
    private UserInfoBase createUser;
    @Transient
    private UserInfoBase acceptUser;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;

    //删除标记

    //判断是否在优惠券在活动期
    @Transient
    private int isCouponActivityTime;


    //赠送多少钱优惠券
   @Transient
    private Integer couPonDenomination;

    public int getIsActivityTime() {
        return isCouponActivityTime;
    }

    public void setIsActivityTime(int isActivityTime) {
        this.isCouponActivityTime = isActivityTime;
    }
}
