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
 * 摇一摇
 * Created by Administrator on 2016/12/28.
 */
@Entity
@Table(name = "shake")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Shake {

    @Id
    private String fkUserId;


    @Column(name = "x")
    private String x;

    @Column(name = "y")
    private String y;


    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
