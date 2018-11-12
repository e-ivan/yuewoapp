package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户推荐
 * Created by hh on 2017.8.7 0007.
 */
@Entity
@Table(name = "user_recommend")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;      //注册用户id

    private String referrerId;  //推荐人id

    private Date created;       //注册时间

    private String recommendCode;//推荐码

}
