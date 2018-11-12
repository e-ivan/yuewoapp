package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 需求请求
 * Created by hh on 2017.9.21 0021.
 */
@Entity
@Table(name = "demand_request")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class DemandRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String budgetFund;
    private String nickname;
    private String mobile;
    private Timestamp created;

}
