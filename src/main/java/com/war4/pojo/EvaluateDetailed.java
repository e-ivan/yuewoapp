package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * 评价清单
 * Created by hh on 2017.11.3 0003.
 */
@Entity
@Table(name = "evaluate_detailed")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class EvaluateDetailed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String orderType;

    private String utterer;     //发表人

    private String receiver;    //接受人

    private Integer type;

    private String title;

    private String content;

    private Integer score;

    private Long evaluateTypeId;

    private String itemId;

    private Timestamp created;

    @Transient
    private List<Long> itemIds;

}
