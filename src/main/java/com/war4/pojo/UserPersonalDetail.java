package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户个人信息
 * Created by hh on 2017.8.30 0030.
 */
@Entity
@Table(name = "user_personal_detail")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserPersonalDetail {
    @Id
    private String userId;
    private String job;
    private String jobField;
    private String company;
    private String hometown;
    private String music;
    private String food;
    private String sport;
    private String book;
    private String travel;
    private String tag;
    private String education;
    private String income;
    private String dateImpression;
    private Date created;
    private Date updated;

    //用户电影收藏
    @Transient
    private List<FilmCollection> filmCollectionList = new ArrayList<>();
    @Transient
    private List<Map<String,Object>> nearbyDatingList = new ArrayList<>();
}
