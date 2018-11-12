package com.war4.pojo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.war4.vo.MomentFileVO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 朋友圈
 */
@Entity
@Table(name = "moment")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Moment {
    public static final int NORMAL = 1; //正常
    public static final int DELETE = 0; //删除

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Transient
    private String nickname;

    @Transient
    private String headImg;

    private Date created;

    @JsonIgnore
    private String image;

    private Integer voteCount = 0;

    private Integer imageCount = 0;

    private Integer commentCount = 0;

    private Integer viewCount = 0;

    public  List<MomentFileVO> getPhotos(){
        if (StringUtils.isBlank(image)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(image,MomentFileVO.class);
    }

    @Transient
    private List<MomentComment> comments = new ArrayList<>();//评论集合

    @Transient
    private List<MomentVoteUser> voteUsers = new ArrayList<>();//点赞用户

    @JsonIgnore
    private Date voteTime;

    @JsonIgnore
    private Date commentTime;

    @JsonIgnore
    private String ip;

    @JsonIgnore
    private String lng;

    @JsonIgnore
    private String lat;

    private Integer state;

    private String content;


    private Integer type = 0;   //类型   0：无图  1：有图

    @Transient
    private boolean isVote;

}