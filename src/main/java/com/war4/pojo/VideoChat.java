package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.war4.base.PropertiesConfig;
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
@Table(name = "video_chat")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter@Setter
public class VideoChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long videoId;

    private Boolean onOff;

    private Integer chatState;

    private Integer chatNum;

    private Timestamp createTime;

    private Integer status;

    private Integer orderCount;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastPayTime;

    @Transient
    private String userPhotoHead;

    @Transient
    private String name;

    @Version
    @JsonIgnore
    private Integer version;

    @Transient
    private UserVideo userVideo;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reloadTime;

    public BigDecimal getVideoChatCostStartStep(){
        return PropertiesConfig.getVideoChatCostStartStep();
    }

    public BigDecimal getVideoChatCostOneStep(){
        return PropertiesConfig.getVideoChatCostOneStep();
    }

}




