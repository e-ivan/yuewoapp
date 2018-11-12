package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 黑名单
 * Created by Administrator on 2016/12/13.
 */
@Entity
@Table(name = "user_black")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserBlack {

    @Id
    private String id;

    @Column(name = "fk_user_id")
    private String fkUserId;

    @Column(name = "black_user_id")
    private String blackUserId;

    @Transient
    private UserInfoBase blackUser;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
