package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 朋友圈点赞用户
 */
@Entity
@Table(name = "moment_vote_user")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class  MomentVoteUser {
    public static final int NORMAL = 1; //正常
    public static final int DELETE = 0; //取消

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    private Long momentId;

    private String userId;

    private String nickname;

    private String headImg;

    @JsonIgnore
    private Date created;

    @JsonIgnore
    private Date operationTime;

    @JsonIgnore
    private Integer state;

}