package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 朋友圈评论
 */
@Entity
@Table(name = "moment_comment")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MomentComment {
    public static final int NORMAL = 1; //正常
    public static final int DELETE = 0; //删除

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long momentId;

    private String userId;

    private String nickname;

    private String headImg;

    private String replyUserId;

    private String replyUserNickname;

    private String replyUserHeadImg;

    private Date created;

    @JsonIgnore
    private Integer state;

    private String content;
}