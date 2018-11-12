package com.war4.base;

import com.war4.enums.CommonErrorResult;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by dly on 2016/8/3.
 */
@Getter@Setter
public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = -8125089607539418832L;
    private Integer errorCode;
    private String exMessage;

    public BusinessException(CommonErrorResult errorResult,String exMessage){
        super(exMessage);
        this.errorCode = errorResult.getCode();
        this.exMessage = exMessage;
    }
}
