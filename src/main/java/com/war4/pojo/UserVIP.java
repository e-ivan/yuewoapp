package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/12/26.
 */

@Entity
@Table(name = "user_vip")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserVIP {
    @Id
    private String fkUserId;

    @Column(name = "vip_setting_id")
    private String vipSettingId;

    @Column(name = "integral")
    private Integer integral;

    @Transient
    private UserVipSetting userVipSetting;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
