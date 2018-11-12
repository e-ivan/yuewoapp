package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 收藏关联
 * Created by hh on 2017.7.14 0014.
 */
@Entity
@Table(name = "collect_mapping")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class CollectMapping {
    public static final int COLLECTION = 1;         //收藏中
    public static final int CANCLE_COLLECT = 0;     //取消收藏


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String userId;      //收藏用户id

    private String objectId;    //收藏品id

    private String type;        //类型

    private Date collectTime;   //收藏时间

    private String picture;     //图片

    private String summary;     //摘要

    private int state;          //收藏状态

    private Date cancelTime;    //取消收藏时间

}
