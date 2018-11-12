package com.war4.vo.film;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 取票信息
 * Created by hh on 2017.8.29 0029.
 */
@Getter
@Setter
public class YingTicketVO {
    private String ticketFlag1;
    private String ticketFlag2;
    private List<YingSellInfoVO> sellInfo;

}
