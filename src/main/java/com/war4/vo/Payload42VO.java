package com.war4.vo;


import lombok.Getter;
import lombok.Setter;

/**
 * description
 *
 * @author wang
 * @since 2017/11/9
 */
@Getter@Setter
public class Payload42VO {
    
    private String startBussinessDate;
    private String endBussinessDate;
    private String cinemaCode;
    private String screenCode;
    private String filmCode;
    private String sessionCode;
    private String code;
    private String startDateTime;
    private String endDateTIme;
    
    /**
     * 1，所有数据 2，未上报的原始电影票票务数据 3，已上报的原始电影票票务数据
     */
    private byte flag;
}
