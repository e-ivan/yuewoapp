package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 活动
 * Created by Administrator on 2016/12/7.
 */

@Entity
@Table(name = "video_chat_report")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class VideoChatReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cid;

    private String createUserId;

    private String reportUserId;

    private Integer type;

    private Timestamp createTime;
}




