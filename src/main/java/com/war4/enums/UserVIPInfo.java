package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/26.
 */
@Getter
@AllArgsConstructor
public enum UserVIPInfo {
    VIP_0("VIP0",0,0),
    VIP_1("VIP1",5000,5),
    VIP_2("VIP2",10000,10),
    VIP_3("VIP3",15000,15),
    VIP_4("VIP4",20000,20),
    VIP_5("VIP5",25000,25),
    VIP_6("VIP6",30000,30);

    public static UserVIPInfo getByValue(String code){
        for(UserVIPInfo vipInfo: UserVIPInfo.values()){
            if (vipInfo.getVip().equals(code)){
                return vipInfo;
            }
        }
        return null;
    }

    private String  vip;
    private Integer aomut;
    private Integer coupon;

}
