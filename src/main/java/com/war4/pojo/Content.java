package com.war4.pojo;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity     //标记为实体类
@Table(name = "content")    //表名
@DynamicInsert(true)        //动态插入
@DynamicUpdate(true)        //动态更新
@Getter
@Setter
public class Content {
    public static final Integer DELETE = 0 ;   //删除
    public static final Integer DRAFT = 1 ;   //草稿
    public static final Integer PUBLISH = 1 << 1;  //发布

    @Id
    private Long id;
    @Column(name = "module")
    private String module;      //模块

    @Column(name = "link_to")
    private String linkTo;      //链接到

    @Column(name = "covers")
    private String covers;      //封面图片

    @Column(name = "cover_type")
    private String coverType;   //封面类型

    @Column(name = "isquality")
    private Integer isQuality = 0;     //是否精选

    @Column(name = "author_id")
    private String authorId;      //作者id

    @Column(name = "author_name")
    private String authorName;  //作者名称

    @Column(name = "ip")
    private String ip;

    @Column(name = "created")
    private Date created;

    @Column(name = "modified")
    private Date modified;      //最后修改时间

    @Column(name = "film_id")
    private String filmId;

    @Column(name = "status")
    private Integer status = DRAFT;

    @Column(name = "comment_status")
    private Integer commentStatus = 0;//评论状态

    @Column(name = "comment_count")
    private Integer commentCount = 0;//评论总量

    @Column(name = "comment_time")
    private Date commentTime;   //最后评论时间

    @Column(name = "view_count")
    private Integer viewCount = 0;  //总访问量

    @Column(name = "collect_count")
    private Integer collectCount = 0;  //收藏次数

    @Column(name = "order_number")
    private Integer orderNumber = 0;//排序编号

    @Column(name = "real_vote")
    private Integer realVote = 0;     //真实支持人数

    @Column(name = "vote_up")
    private Integer voteUp = 0;     //虚拟支持人数

    @Column(name = "vote_down")
    private Integer voteDown = 0;   //反对人数

    @Column(name = "rate")
    private Integer rate = 0;       //评分分数

    @Column(name = "rate_count")
    private Integer rateCount = 0;  //评分次数

    @Column(name = "lng")
    private BigDecimal lng;     //经度

    @Column(name = "lat")
    private BigDecimal lat;     //纬度

    @Column(name = "type")
    private Integer type;        //文章类型

    @Column(name = "title")
    private String title;       //文章标题

    @Column(name = "text")
    private String text;        //文章内容

    @Column(name = "summary")
    private String summary;     //文章摘要

    @Column(name = "remarks")
    private String remarks;     //备注


    @Transient  //使hibernate忽略此字段
    private List<Attachment> coverList = new ArrayList<>();


    public List<Attachment> getCoverList() {
        return JSON.parseArray(covers, com.war4.pojo.Attachment.class);
    }

}
