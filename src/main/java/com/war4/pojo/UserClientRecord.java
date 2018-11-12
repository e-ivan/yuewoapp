package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 用户登录设备信息
 */
@Entity
@Table(name = "user_client_record")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserClientRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String imei;

    private String model;

    private String os;

    private Timestamp created;

    private Date lastLogin;

    private Integer logins;
}