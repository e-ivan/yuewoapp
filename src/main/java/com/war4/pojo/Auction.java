package com.war4.pojo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.war4.base.BaseAuditDomain;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 竞拍活动
 * Created by hh on 2017.10.30 0030.
 */
@Entity
@Table(name = "auction")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Auction extends BaseAuditDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String theme;

    @JsonIgnore
    private String album;

//    private String content;

    private Integer type;

    private Integer paySite;

//    private Double duration;

    private String cinemaId;      //电影院id

    private String movieName;   //观看的电影名称

    private Date planTime;      //电影场次时间

    private BigDecimal auctionPrice;//起拍价格

//    private BigDecimal deposit;     //押金

    private String site;//地点

    private String lng;//经度

    private String lat;//纬度

    @DateTimeFormat(pattern = "yyyy-MM-dd HH")
    private Date finalTime;//最后竞拍时间

    private String bidderId;//当前竞拍人

    private Long itemId;//出价明细ID

    private BigDecimal bidPrice;//出价

    private Integer viewCount;  //浏览次数

    private Integer bidCount;   //竞拍次数

    private String filmOrderId; //电影订单id

    private String movieImg;    //电影图片

    @Transient
    private UserInfoBase user;

    @Transient
    private Integer joinState;      //参与状态

    public List<String> getAlbums(){
        return JSON.parseArray(album,String.class);
    }
    public Date getCurrentTime(){
        return new Date();
    }
}
