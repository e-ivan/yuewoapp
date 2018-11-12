package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * 附件内容
 */
@Getter
@Setter
public class Attachment{

    private String text;    //内容

    @JsonIgnore
    private Long userId;    //上传用户

    @JsonIgnore
    private Long contentId; //关联文章

    private String path;    //路径

    @JsonIgnore
    private String mime;    //mime

    @JsonIgnore
    private String suffix;  //后缀

    @JsonIgnore
    private String type;    //类型

    @JsonIgnore
    private Long lat;

    @JsonIgnore
    private Long lng;

    @JsonIgnore
    private String title;   //标题
}