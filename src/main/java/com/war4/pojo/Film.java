package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 电影
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "film")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Film {

    @Id
    private String id;

    @Column(name = "name")
    private String name;
    @Column(name = "place")
    private String place;
    @Column(name = "film_time")
    private Integer filmTime;
    @Column(name = "release_time")
    private String releaseTime;
    @Column(name = "intro")
    private String intro;
    @Column(name = "price")
    private BigDecimal price;


    @Column(name = "status")
    private Integer status;

    @Transient
    private String photoUrl;

    @Transient
    private String filmUrl;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
