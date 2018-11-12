package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 推送消息类型
 * Created by hh on 2017.7.12 0012.
 */
@Getter
@AllArgsConstructor
public enum MessageLogType {
    SYSTEM_UPDATE(0,"systemUpdate","系统更新"),
    FILM_ORDER(1,"filmOrder","电影订单"),
    ARTICLE_COMMENT(2,"articleComment","资讯评论"),
    DATING_FILM(3,"datingFilm","约会"),
    MOMENT_COMMENT(4,"momentComment","朋友圈评论"),
    FILM_TOPIC_COMMENT(5,"filmTopicComment","电影话题评论"),
    FRIEND_APPLY(6,"friendApply","好友申请"),
    INVITE_USER(7,"inviteUser","邀请用户"),
    MARKER_COUPON(8,"markerCoupon","商城代金券"),
    MARKER_ORDER(9,"markerOrder","商城订单"),
    BID_ORDER(10,"bidOrder","投标订单"),
    VIDEO_CHAT(11,"videoChat","视频聊天"),
    AUCTION(12,"auction","红人竞拍"),
    AUTHENTICATION(13,"authentication","实名认证"),
    NEARBY_DATING(14,"nearbyDating","附近有约"),
    FILM_COUPON(15,"filmCoupon","电影优惠券"),
    ;

    private Integer code;
    private String value;
    private String MessageLogName;

    public static String getValueByCode(Integer code){
        for (MessageLogType messageLogType : MessageLogType.values()) {
            if (messageLogType.getCode().equals(code)){
                return messageLogType.getValue();
            }
        }
        return null;
    }
}
