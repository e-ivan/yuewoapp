package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 角色申请表
 * Created by Administrator on 2016/12/22.
 */
@Entity
@Table(name = "recruit_apply")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class RecruitApply {

    @Id
    private String id;


    @Column(name = "recruit_id")
    private String recruitId;
    @Column(name = "fk_user_id")
    private String fkUserId;
    @Column(name = "apply_type")
    private String applyType;


    @Transient
    private ActorResume actorResume ;

    @Transient
    private DirectorResume directorResume;

    @Transient
    private SeceenwriterResume seceenwriterResume;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
