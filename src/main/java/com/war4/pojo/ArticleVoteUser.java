package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "article_vote_user")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class ArticleVoteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentId;

    private String userId;

    private Date created;

    private Date operateTime;

    private Integer state;

    private int count;
}