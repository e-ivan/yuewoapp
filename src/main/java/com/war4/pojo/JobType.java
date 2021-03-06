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
 * Created by Administrator on 2017/5/10.
 */@Entity
@Table(name = "job_type")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class JobType {
    @Id
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "state")
    private Integer state;
    @Column(name = "del_flag")
    private Integer delFlag;
}
