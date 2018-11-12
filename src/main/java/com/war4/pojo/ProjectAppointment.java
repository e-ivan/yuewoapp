package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 投资预约表
 * Created by Administrator on 2016/12/22.
 */
@Entity
@Table(name = "project_appointment")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class ProjectAppointment {
    @Id
    private String id;


    @Column(name = "project_id")
    private String projectId;
    @Column(name = "fk_user_id")
    private String fkUserId;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "link_name")
    private String linkName;

    @Column(name = "link_phone")
    private String linkPhone;
    @Column(name = "link_post")
    private String linkPost;
    @Column(name = "bankroll")
    private String bankroll;
    @Column(name = "intention_investment_money")
    private BigDecimal intentionInvestmentMoney;

    @Column(name = "investment_case")
    private String investmentCase;
    @Column(name = "note")
    private String note;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
