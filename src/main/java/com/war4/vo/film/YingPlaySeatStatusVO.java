package com.war4.vo.film;

import lombok.Getter;
import lombok.Setter;

/**
 * 场次座位信息
 * Created by hh on 2017.8.28 0028.
 */
@Getter
@Setter
public class YingPlaySeatStatusVO {
    private Integer x;
    private Integer y;
    private String rowValue;
    private String columnValue;
    private Long cineSeatId;
    private String seatStatus;
    private String type;
    private String pairValue;
    private Long areaId;

}
