package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by dly on 2016/8/22.
 */
@Entity
@Table(name = "temp_valid_code")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class ValidCode {
    public static Integer VALID_MINUTE = 5;
    public static Integer CANNOT_RESEND_MINUTE = 1;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "phone")
    private String phone;
    @Column(name = "valid_code")
    private String validCode;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
