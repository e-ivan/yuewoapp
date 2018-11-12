package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 直播间
 * Created by Administrator on 2016/12/7.
 */
@Entity
@Table(name = "anchor_room")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class AnchorRoom {
    @Id
    private String id;

    @Column(name = "fk_user_id")
    private String fkUserId;

    @Column(name = "cid")
    private String cid;

    @Column(name = "chatroom_id")
    private String chatRoomId;

    @Column(name = "name")
    private String name;
    @Column(name = "push_url")
    private String pushUrl;
    @Column(name = "http_pull_url")
    private String httpPullUrl;
    @Column(name = "hls_pull_url")
    private String hlsPullUrl;
    @Column(name = "rtmp_pull_url")
    private String rtmpPullUrl;

    @javax.persistence.Transient
    private String photoUrl;

    @Transient
    private UserInfoBase userInfoBase;

    @Column(name = "total")
    private Integer total;

    @Column(name = "role")
    private Integer role;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
