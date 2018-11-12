package com.war4.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 查询约会人员参数
 * Created by E_Iva on 2018.1.17.0017.
 */
@Getter@Setter
public class QueryDatingUserVO {
    private String userId;
    private String lng;
    private String lat;
    private Integer minDist;
    private Integer maxDist;
    private Integer startAge;
    private Integer endAge;
    private Integer sex;
    private Integer internal;
    private Integer page;
    private Integer limit;
}
