package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户浏览历史
 * Created by hh on 2017.7.25 0025.
 */
@Entity
@Table(name = "user_view_history")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter@NoArgsConstructor
public class UserViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String userId;  //用户id

    private Long contentId; //文章id

    private String module;  //文章所属版块

    private Date created;

    private Date recentViewed;//最后浏览时间

    private int viewCount;      //总共浏览次数

    @Transient
    private Content content;    //文章详情

    public UserViewHistory(String userId, Long contentId, String module) {
        this.userId = userId;
        this.contentId = contentId;
        this.module = module;
    }
}