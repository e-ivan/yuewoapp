package com.war4.pojo;

import com.war4.base.BaseAuditDomain;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 活动
 * Created by Administrator on 2016/12/7.
 */

@Entity
@Table(name = "internet_star")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class InternetStar extends BaseAuditDomain{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long aid;

    private Long vid;

    private Integer type;

    @Transient
    private UserVideo userVideo;
}
