package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dly on 2016/9/28.
 */
@Entity
@Table(name = "message_friend_apply")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MessageFriendApply {
    @Id
    private String id;

    @Column(name = "fk_from_user_id")
    private String fkFromUserId;
    @Column(name = "fk_to_user_id")
    private String fkToUserId;
    @Column(name = "message_content")
    private String messageContent;
    @Column(name = "status")
    private Integer status;

    @Transient
    private UserInfoBase fromUser;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
