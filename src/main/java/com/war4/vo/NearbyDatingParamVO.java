package com.war4.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 附近有约请求参数
 * Created by E_Iva on 2018.3.23.0023.
 */
@Getter@Setter
public class NearbyDatingParamVO {
    private String movieName;
    private String pic;
    private String cinemaId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date datingDate;
    private Integer sex;
    private String content;
    private Integer paySite;
}
