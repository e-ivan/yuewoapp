package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收藏状态
 * Created by hh on 2017.7.14 0014.
 */
@Getter
@AllArgsConstructor
public enum CollectState {
    COLLECTION(1,"收藏中"),
    CANCEL_COLLECT(0,"收藏取消");
    private int value;
    private String stateName;

}
