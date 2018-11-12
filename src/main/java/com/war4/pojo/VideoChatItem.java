package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 活动
 * Created by Administrator on 2016/12/7.
 */

@Entity
@Table(name = "video_chat_item")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter@Setter
public class VideoChatItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cid;

    private String createUserId;

    private String acceptUserId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    private Long duration;

    private Timestamp createTime;

    private String vid; //网易云视频id

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    private String outTradeNo;

    private String payType;

    private BigDecimal payMoney;

    private String orderId;

    private Integer status;

    private String hangUpUserId;

    @Version
    @JsonIgnore
    private Integer version;

    @Transient
    private String userPhotoHead;

    @Transient
    private String name;

    @Transient
    private Integer age = 0;

    @Transient
    private Integer sex = -1;

}




