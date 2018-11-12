package com.war4.vo.film;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 影厅座位
 * Created by hh on 2017.8.25 0025.
 */
@Document(collection = "CINEMA_HALL_SEAT")
@Getter
@Setter
public class YingCinemaHallSeatVO extends BaseVO {
    private Long cineSeatId;
    private String cinemaId;
    private Long xCoord;
    private Long yCoord;
    private String loveseats;
    private String row;
    private String column;
    private String status;
    private String type;
    private Long areaId;

}
