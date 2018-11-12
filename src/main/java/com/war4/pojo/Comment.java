package com.war4.pojo;
/**
 * 评论
 */

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comment")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Comment {
    public static final int NORMAL = 0;     //正常
    public static final int AUDITING = 1;   //审核中

    @GeneratedValue(strategy = GenerationType.IDENTITY)//id的生成策略
    @Id
    private Long id;

    private Long parentId;      //回复评论id

    private Long contentId;     //文章内容id

    private Integer commentCount = 0;//评论次数

    private String contentModule;//评论所在的模块

    private Integer orderNumber;//排序

    private String userId;

    @Column(name = "user_photo_head")
    private String userPhotoHead;

    private String author;      //评论作者

    private String ip;

    private String agent;       //提交评论的浏览器信息

    private Date created;

    private Integer status;     //评论的状态   0：正常   1:审核中

    private Integer voteUp = 0;     //顶的数量

    private Integer voteDown = 0;   //踩的数量

    private Long lng;

    private Long lat;

    private String text;        //评论内容

    @Transient
    private Comment parent;     //回复评论

}