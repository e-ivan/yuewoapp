package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 活动
 * Created by Administrator on 2016/12/7.
 */

@Entity
@Table(name = "enroll_form")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class EnrollForm {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;


    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private String sex;

    @Column(name = "birth")
    private String birth;

    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "id_card")
    private String idCard;

    @Column(name = "anwser")
    private String anwser;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "album_flag")
    private String albumFlag;

    @Transient
    private String idCardUrl;

    @Transient
    private List<FileUpload> albumList = new ArrayList<>(); //个人相册

}
