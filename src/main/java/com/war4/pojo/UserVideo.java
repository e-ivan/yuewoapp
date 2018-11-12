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
@Table(name = "user_video")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserVideo extends BaseAuditDomain{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String video;

}
