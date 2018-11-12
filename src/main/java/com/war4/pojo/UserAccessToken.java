package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by dly on 2016/11/25.
 */
@Entity
@Table(name = "user_access_token")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserAccessToken {

    @Id
    private String fkUserId;

    @Column(name = "access_token")
    private String accessToken;


    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
