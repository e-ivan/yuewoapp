package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 用户分享记录
 * Created by E_Iva on 2018.1.4.0004.
 */
@Entity
@Table(name = "user_share_record")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserShareRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;      //用户id

    private String url;

    private Integer type;

    private String objectId;

    private Timestamp created;
}
