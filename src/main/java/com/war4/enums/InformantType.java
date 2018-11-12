package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 举报中心类型
 * Created by hh on 2017.9.8 0008.
 */
@Getter
@AllArgsConstructor
public enum InformantType {
    COMMON(0,"common","通用"),
    FILM_COMMENT(1,"filmComment","影评"),
    USER_DETAIL(2,"userDetail","个人信息"),
    FILM_TOPIC(3,"filmTopic","话题");
    public static InformantType getByCode(Integer code){
        for(InformantType informantType: InformantType.values()){
            if (informantType.getCode().equals(code)){
                return informantType;
            }
        }
        return null;
    }

    private Integer code;
    private String value;
    private String MessageLogName;
}
