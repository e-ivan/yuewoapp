package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/1/3.
 */
@Entity
@Table(name = "film_room_user")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class FilmRoomUser {

    @Id
    private String id;

    @Column(name = "fk_room_id")
    private String fkRoomId;

    @Column(name = "fk_user_id")
    private String fkUserId;

    @Transient
    private UserInfoBase user;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
