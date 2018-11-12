package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态
 * Created by Administrator on 2016/12/15.
 */
@Getter
@AllArgsConstructor
public enum AuditStatus {
    CREATE(10,"已创建"),
    PASS(20,"审核通过"),
    REFUSE(1000,"审核拒绝"),
    AWAIT(30,"等候审核");

    private Integer code;
    private String value;

    public static AuditStatus getByCode(Integer code){
        for(AuditStatus status: AuditStatus.values()){
            if (status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
