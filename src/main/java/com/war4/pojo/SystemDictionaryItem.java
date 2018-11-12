package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 数据字典明细
 * Created by hh on 2017.6.23 0023.
 */
@Entity
@Table(name = "system_dictionary_item")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class SystemDictionaryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;

    @Column(name = "field")
    private String field;       //字段

    @Column(name = "value")
    private Integer value;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "expression")
    private String expression;      //公式、表达式、规则

    @Column(name = "status")
    private Integer status;         //状态  0:停用  1:正常

    @Column(name = "parentId")
    private Long parentId;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
}
