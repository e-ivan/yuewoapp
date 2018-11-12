package com.war4.vo.film;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 影院配置信息
 * Created by hh on 2017.8.25 0025.
 */
@Document(collection = "CINEMA_CONFIG")
@Getter
@Setter
public class YingCinemaConfigVO extends BaseVO{
    private Long id;
    private String cinemaId;
    private String cinemaName;
    private Integer buyNumLimit;
    private Double handleFee;
    private Integer playPeriod;
    private Integer forbidRefundMin;
    private String validPeriod;
    private Integer sellTimeLimit;
    private Double cineHandleFee;
}
