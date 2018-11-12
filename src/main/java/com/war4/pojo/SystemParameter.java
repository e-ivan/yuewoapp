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
 * 系统参数
 * Created by Administrator on 2016/12/29.
 */
@Entity
@Table(name = "system_parameter")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class SystemParameter {
    @Id
    private String id;

    @Column(name = "service_phone")
    private String servicePhone;

    @Column(name = "about_us")
    private String aboutUs;
    @Column(name = "help")
    private String help;


    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
