package com.war4.vo.film;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 影院信息
 * Created by hh on 2017.8.24 0024.
 */
@Document(collection = "CINEMA")
@Getter
@Setter
public class YingCinemaVO extends BaseVO{
    private String cinemaId;
    private String cinemaName;
    private String validPeriod;
    private String cinemaNumber;
    private Integer state;//关闭状态
    private Integer source;//影院所属
    private boolean relation;
}
