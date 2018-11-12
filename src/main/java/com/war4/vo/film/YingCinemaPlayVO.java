package com.war4.vo.film;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 天智创客场次
 * Created by hh on 2017.8.23 0023.
 */
@Document(collection = "CINEMA_PLAY")
@Getter
@Setter
public class YingCinemaPlayVO extends BaseVO{
    private Long id;
    private Long planId;
    private String cinemaId;
    private Integer seatAvailableNum;
    private Double marketPrice;
    private Double lowestPrice;
    private Integer priceType;
    private Integer seatTotalNum;
    private String businessDate;
    private String cineUpdateTime;
    private Long hallId;
    private Double price;
    private Integer allowBook;
    private String startTime;
    private String endTime;
    private String hallName;
    private Long cinePlayId;
    private Double leyingPrice;     //乐影价
    private Double partnerPrice;    //鼎新电商平台启用算出的价格,如果有则售价不能低于此价
    private List<YingMovieInfoVO> movieInfo;
    private List<YingAreaInfoVO> areaInfo;

}
