package com.war4.pojo;

import com.war4.vo.SimpleUserVO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * 评论系统
 * Created by E_Iva on 2018.4.11.0011.
 */
@Entity
@Table(name = "comment_system")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class CommentSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String objectId;

    private Long parentId;

    private String userId;

    private String replyUserId;

    private String content;

    private Integer state;

    private Integer replyCount;

    private Integer type;

    private Timestamp created;

    @Transient
    private SimpleUserVO user;

    @Transient
    private SimpleUserVO replyUser;

    @Transient
    private List<CommentSystem> replys;
}
