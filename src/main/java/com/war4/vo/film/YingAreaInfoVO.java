package com.war4.vo.film;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 区域价格列表
 * Created by hh on 2017.8.23 0023.
 */
@Getter
@Setter
public class YingAreaInfoVO extends BaseVO {
    private Long seatId;
    private Long areaId;
    private String areaName;    //影厅区域名称
    private Double areaPrice;   //影厅区域座位售卖价格
    private Double areaServiceFee;  //当前区域下的单个座位服务费
    private List<YingEcardVO> ecardPrices;
}
