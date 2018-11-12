package com.war4.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 售票原始数据结构
 *
 * @author wang
 * @since 2017/11/8
 */
@Getter@Setter
public class SaleTicketVO implements Serializable{
    private static final long serialVersionUID = -6735324780164171021L;
    /**
     * 营业日期 YYYY-MM-DD
     */
    private String bussinessDate;
    
    private String cinemaCode;
    
    private String onlineSaleCode;
    
    private String screenCode;
    
    private String filmCode;
    
    private String sessionCode;
    
    private String sessionDateTime;
    
    /**
     * 1，售票2，退票3，预售
     */
    private byte operation;
    
    private String code;
    
    private String seatCode;
    
    private float price;
    
    private float serviceFee;
    
    /**
     * 取值固定为1
     */
    private byte onlineSale = 0x01;
    
    private String dateTime;
}
