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
 * 地址省
 * Created by Administrator on 2016/10/21.
 */
@Entity
@Table(name = "lk_provinces")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class LKProvinces {
    @Id
    private  Integer id;

    @Column(name = "province_id")
    private String provincesId;

    @Column(name = "province")
    private String provinces;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
