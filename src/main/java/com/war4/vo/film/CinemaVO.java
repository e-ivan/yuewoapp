package com.war4.vo.film;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 影院信息
 * Created by hh on 2017.8.17 0017.
 */
@Document(collection = "CINEMA_DETAIL")
@Getter
@Setter
public class CinemaVO extends BaseVO{
    private String cinemaAddress;
    private String cinemaId;
    private String cinemaName;
    private String cinemaTel;
    private Long cityId;
    private String cityName;
    private Long districtId;
    private String districtName;
    private Long countryId;
    private String latitude;
    private String logo;
    private String longitude;
    private String cinemaIntro;
    private Integer source;
    private Double distance;
    private Integer state;
    private String openTime;
    private String foreignCinemaId;
    private String cityCode;
}
