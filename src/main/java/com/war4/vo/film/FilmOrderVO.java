package com.war4.vo.film;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by hh on 2017.9.6 0006.
 */
@Document(collection = "FILM_ORDER")
@Getter
@Setter
public class FilmOrderVO extends BaseVO {
    private String orderId;
    private Long planId;
    @JsonIgnore
    private Long channelId;
    private Integer orderStatus;
    private Integer count;
    private Date orderTime;
    private String ticketNo;
    private Double money;
    private String mobile;
    private String seatInfo;
    private String seatNo;
    private Double unitPrice;
    private String machineType;
    private String finalTicketNo;
    private String finalVerifyCode;//与ticketNo相同
    @JsonIgnore
    private String userId;//用户id
    private CinemaPlanVO plan;
}
