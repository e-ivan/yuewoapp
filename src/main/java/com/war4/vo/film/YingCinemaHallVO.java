package com.war4.vo.film;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 影厅详情
 * Created by hh on 2017.8.25 0025.
 */
@Document(collection = "CINEMA_HALL")
@Getter
@Setter
public class YingCinemaHallVO extends BaseVO {
    private Long id;
    private Long cineHallId;
    private String cinemaId;
    private String name;
    private Integer seatNum;
    private String type;
    private String audioType;
    private List<YingAreaInfoVO> areaInfo;
}
