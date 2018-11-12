package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/13.
 */
@Getter
@AllArgsConstructor
public enum ActivityBidStatus {

    CREATE(0,"未开始"),
    BEGIN(1,"开始"),
    FINISH(2,"结束"),
    ALL(3,"全部"),  //给 app
    GOING(4,"进行中"),
    FULL(5,"满人"),
    NOTOVER(6,"非结束");  //给后台定时扫描用


    public static ActivityBidStatus getByValue(Integer code){
        for(ActivityBidStatus status: ActivityBidStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }

    private Integer code;
    private String value;

}
