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

/**
 * 系统消息
 * Created by Administrator on 2016/12/13.
 */

@Entity
@Table(name = "message_sys")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MessageSys {
    @Id
    private String id;
    @Column(name = "content")
    private String content;
    @Column(name = "fk_user_id")
    private String fkUserId;


    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
