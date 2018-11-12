package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * 订单基本表
 * Created by Administrator on 2016/11/10.
 */
@Entity
@Table(name = "base_order")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class BaseOrder {

    @Id
    private String id;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private Integer status;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

    private String creator;                                 //创建人

    private String receiver;                                //接受人

}
