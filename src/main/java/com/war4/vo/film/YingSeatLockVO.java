package com.war4.vo.film;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by hh on 2017.8.31 0031.
 */
@Getter
@Setter
public class YingSeatLockVO {
    private String lockFlag;
    private Double partnerPrice;
    private List<YingAreaInfoVO> areaInfo;
    private List<YingBuySeatVO> seats;  //购买时需要的座位数据
}
