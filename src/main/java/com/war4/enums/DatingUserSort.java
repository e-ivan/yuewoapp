package com.war4.enums;

import com.war4.pojo.Gift;
import com.war4.pojo.UserInfoBase;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Administrator on 2016/12/20.
 */
@Getter
@AllArgsConstructor
public enum DatingUserSort {



    USER_HOST("userHost",Gift.class),//

    USER_DISTANCE("userDistance",UserInfoBase.class);//


    private String code;
    private Class belongToClass;

    public static DatingUserSort getByCode(String code){
        for(DatingUserSort sort: DatingUserSort.values()){
            if (sort.getCode().equals(code)){
                return sort;
            }
        }
        return null;
    }
}
