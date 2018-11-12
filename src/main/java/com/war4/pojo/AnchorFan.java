package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 主播粉丝
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "anchor_fan")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class AnchorFan {
    @Id
    private String id;

    //主播Id
    @Column(name = "anchor_user_id")
    private String anchorUserId;

    //粉丝Id
    @Column(name = "fan_user_id")
    private String fanUserId;

    //粉丝
    @Transient
    private UserInfoBase userFan;

    //主播
    @Transient
    private UserInfoBase userAnchor;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
