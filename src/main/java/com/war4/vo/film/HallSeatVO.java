package com.war4.vo.film;

import com.war4.base.BaseVO;
import com.war4.vo.UserInHallSeatVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 影厅座位
 * Created by hh on 2017.9.2 0002.
 */
@Getter
@Setter
public class HallSeatVO extends BaseVO {
    private int graphCol;
    private int graphRow;
    private long hallId;
    private String seatCol;
    private String seatRow;
    private String seatNo;
    private int seatState;
    private int seatType;
    private Boolean isLoverL;
    private String seatPieceNo;     //areaId
    private UserInHallSeatVO seatUser;
}
