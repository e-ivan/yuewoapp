package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 投资项目表
 * Created by Administrator on 2016/12/22.
 */
@Entity
@Table(name = "project")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class Project {
    @Id
    private String id;

    @Column(name = "project_name")
    private String projectName;
    @Column(name = "project_money")
    private BigDecimal projectMoney;
    @Column(name = "project_launch")
    private String projectLaunch;
    @Column(name = "project_intro")
    private String projectIntro;

    @Transient
    private String projectPhoto;

    @Transient
    private List<FileUpload> detailPhotos;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
