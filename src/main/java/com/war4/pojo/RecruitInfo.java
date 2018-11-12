package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 发布角色信息
 * Created by Administrator on 2016/12/22.
 */
@Entity
@Table(name = "recruit_info")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class RecruitInfo {
    @Id
    private String id;

    @Column(name = "fk_crew_id")
    private String fkCrewId;
    @Column(name = "role_name")
    private String roleName;
    @Column(name = "role_sex")
    private String roleSex;

    @Column(name = "role_age")
    private String roleAge;
    @Column(name = "role_label")
    private String roleLabel;
    @Column(name = "role_intro")
    private String roleIntro;

    @Column(name = "count")
    private Integer count;

    @Transient
    private Integer applySum;

    @Column(name = "apply_type")
    private String applyType;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
