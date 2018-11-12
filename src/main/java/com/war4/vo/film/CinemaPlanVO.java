package com.war4.vo.film;

import com.war4.base.BaseVO;
import com.war4.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Created by hh on 2017.9.1 0001.
 */
@Getter
@Setter@ToString
public class CinemaPlanVO extends BaseVO {
    private CinemaVO cinema;
    private MovieVO movie;
    private long planId;
    private long movieId;
    private String cinemaId;
    private String countryId;
    private String featureNo;
    private String featureTime;
    private Date featureTimeDate;
    private String hallName;
    private String hallNo;
    private String language;
    private double price;
    private String screenType;
    private double standardPrice;
    private long platform;

    public Date getFeatureTimeDate(){
        if (this.featureTimeDate == null) {
            return DateUtil.parseDate(featureTime,"yyyy-MM-dd HH:mm:ss");
        }
        return featureTimeDate;
    }
}
