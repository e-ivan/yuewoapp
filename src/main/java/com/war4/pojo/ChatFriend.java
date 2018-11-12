package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 好友关系表
 * Created by Administrator on 2016/10/24.
 */
@Entity
@Table(name = "chat_friend")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class ChatFriend {
    @Id
    private String id;


    @Column(name = "fk_user_id")
    private String fkUserId;
    @Column(name = "fk_friend_user_id")
    private String fkFriendUserId;
    @Column(name = "fk_apply_id")
    private String fkApplyId;

    @Column(name = "note")
    private String note;

    @Column(name = "type")
    private Integer type;

    @Transient
    private UserInfoBase userInfoBase;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
