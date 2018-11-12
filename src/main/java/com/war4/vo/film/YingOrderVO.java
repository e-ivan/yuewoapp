package com.war4.vo.film;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 电影订单
 * Created by hh on 2017.9.5 0005.
 */
@Document(collection = "FILM_ORDER_HISTORY")
@Getter
@Setter
public class YingOrderVO {
    private String orderId;
    private YingCinemaPlayVO plan;
    private YingCinemaVO cinema;
    private YingCinemaConfigVO cinemaConfig;
    private YingCinemaHallVO hall;
    private YingMovieInfoVO movie;
    private List<YingCinemaHallSeatVO> seats;
    private YingTicketVO ticket;
    private Date orderTime;
    private String seatNo;
    private YingSeatLockVO seatLock;

}
