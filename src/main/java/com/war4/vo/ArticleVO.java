package com.war4.vo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.war4.pojo.Attachment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章临时对象
 * Created by hh on 2017.7.26 0026.
 */
@Getter
@Setter@NoArgsConstructor
public class ArticleVO {
    private Long contentId;
    @JsonIgnore
    private String covers;      //封面图片
    private String title;       //文章标题
    private String summary;     //文章摘要
    private Integer type;        //文章类型
    private Integer commentCount = 0;//评论总量
    private Integer voteUp = 0;     //支持人数
    private String linkTo;      //文章链接
    private List<Attachment> coverList = new ArrayList<>();

    public ArticleVO(Long contentId, String covers, String title, String summary,Integer type, Integer commentCount, Integer voteUp,String linkTo) {
        this.contentId = contentId;
        this.covers = covers;
        this.title = title;
        this.summary = summary;
        this.type = type;
        this.commentCount = commentCount;
        this.voteUp = voteUp;
        this.linkTo = linkTo;
    }

    public List<Attachment> getCoverList() {
        return JSON.parseArray(covers,Attachment.class);
    }

}
