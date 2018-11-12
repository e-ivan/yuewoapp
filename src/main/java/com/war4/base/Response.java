package com.war4.base;

import com.war4.enums.CommonErrorResult;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * dly
 */
@Getter@Setter
public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    private int errcode;
    private String msg;

    public Response() {
        this.errcode = 0;
        this.msg = "操作成功";
    }

    public Response(String exMsg) {
        this.errcode = 0;
        this.msg = exMsg;
    }

    public Response(CommonErrorResult resultMessage,String exMessage) {
        this.errcode = resultMessage.getCode();
        this.msg = exMessage;
    }

    public Response(BusinessException businessException){
        this.errcode = businessException.getErrorCode();
        this.msg = businessException.getExMessage();
    }

}
