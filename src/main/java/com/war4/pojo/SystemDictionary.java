package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * 数据字典
 * Created by hh on 2017.6.23 0023.
 */
@Entity
@Table(name = "system_dictionary")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class SystemDictionary {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(name = "title")
    private String title;

    @Column(name = "sn")
    private String sn;

    @Column(name = "status")
    private Integer status;

    @Column(name = "value")
    private Integer value;

    @Transient
    private List<SystemDictionaryItem> items;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
}
