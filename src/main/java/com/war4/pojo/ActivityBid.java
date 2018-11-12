package com.war4.pojo;

import com.war4.vo.film.MovieVO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 活动
 * Created by Administrator on 2016/12/7.
 */

@Entity
@Table(name = "activity_bid")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter @Setter
public class ActivityBid {

    public  static final  Byte TYPE_ONEYUAN = 0;  //类型是一元购

    public  static final  Byte STATE_CREATE = 0;  //创建
    public  static final  Byte STATE_GOING = 2;  //进行中
    public  static final  Byte STATE_FULLPEOPLE = 3;  //满人
    public  static final  Byte STATE_OVER = 1;  //结束

    public  static final  Boolean FLAG_NOTENOUGH = false;  //非满人
    public  static final  Boolean FLAG_ENOUGH = true;  //满人状态

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "pic")
    private String pic;

    @Column(name = "created")
    private Timestamp created;

    @Column(name = "type")
    private Byte type;

    @Column(name = "object_id")
    private String objectId;

    @Column(name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Column(name = "end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Column(name = "rated_bid")
    private Integer ratedBid;

    @Column(name = "bid_price")
    private BigDecimal bidPrice;

    @Column(name = "full_proceed")
    private Boolean fullProceed;

    @Column(name = "join_bid")
    private Integer joinBid;

    @Column(name = "odds")
    private BigDecimal odds;

    @Column(name = "victor_count")
    private Integer victorCount;

    @Column(name = "state")
    private Byte state;

    @Column(name = "version")
    @Version
    private Integer version;

    @Transient
    private String currentUser;     //当前查询用户

    @Transient
    private Integer joinState;      //参与状态

    @Transient
    private List<ActivityBidItem> items;    //参与明细

    @Transient
    private MovieVO movie;

}
