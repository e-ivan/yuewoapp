package com.war4.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 宣传广告请求参数
 * Created by E_Iva on 2018.3.29.0029.
 */
@Getter
@Setter
public class AdPageParamVO {
    private Long adId;
    private MultipartFile banner;
    private MultipartFile share;
    private MultipartFile pop;
    private Integer[] types;
    private String title;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    private Integer location;
    private String value;
}
