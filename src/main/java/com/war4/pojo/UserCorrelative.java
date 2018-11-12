package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 与我相关
 * Created by hh on 2017.7.25 0025.
 */
@Entity
@Table(name = "user_correlative")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserCorrelative {
    public static final String VIEWS = "views";
    public static final String FILM_COMMENTS = "filmComments";
    public static final String COLLECTS = "collects";
    public static final String FILM_TOPICS = "filmTopics";
    public static final String FILM_ORDERS = "filmOrders";
    public static final String MOMENTS = "moments";
    public static final String FEED_BACKS = "feedbacks";
    public static final String DATINGS = "datings";
    public static final String ARTICLE_VOTES = "articleVotes";
    public static final String INVITES = "invites";
    public static final String FILM_COUPONS = "filmCoupons";
    public static final String MARKET_COUPONS = "marketCoupons";
    public static final String EVALUATE_GOODS = "evaluateGoods";
    public static final String EVALUATE_GENERALS = "evaluateGenerals";
    public static final String EVALUATE_POORS = "evaluatePoors";
    public static final String VIDEO_CHAT = "videoChat";
    public static final String AUCTION = "auction";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String userId;
    private int views;          //看过
    private int filmComments;   //电影评论
    private int collects;       //收藏
    private int filmTopics;     //电影话题
    private int filmOrders;     //电影订单
    private int moments;        //朋友圈
    private int feedbacks;      //用户反馈
    private int datings;        //约会
    private int articleVotes;   //文章点赞
    private int invites;        //推荐人数
    private int filmCoupons;    //电影优惠券
    private int marketCoupons;  //商城优惠券
    private int evaluateGoods;  //好评
    private int evaluateGenerals;//中评
    private int evaluatePoors;  //差评
    private int videoChat;      //视频聊天
    private int auction;        //竞拍成功

    @Transient
    private UserRatio ratio;

}
