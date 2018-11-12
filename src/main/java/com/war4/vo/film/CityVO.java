package com.war4.vo.film;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 城市信息
 * Created by hh on 2017.9.12 0012.
 */
@Document(collection = "KOU_CITY")
@Getter
@Setter
public class CityVO {
    private int allCinemaCnt;
    private String cityPinYin;
    private int otherCinemaCnt;
    private Long cityId;
    private int cityHot;
    private int couponCinemaCnt;
    private String cityName;
    private int ticketCinemaCnt;
    private String longitude;
    private String latitude;
    private String cityCode;
}
