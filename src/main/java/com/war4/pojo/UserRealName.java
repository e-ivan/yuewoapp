package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */
@Entity
@Table(name = "user_real_name")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class UserRealName {
    @Id
    private String id;
    @Column(name = "province_card")
    private String provinceCard;
    @Column(name = "real_name")
    private String realName;
    @Column(name = "state")
    private Integer state;
    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;
    @Transient
    List<FileUpload> realNameList;
    @Transient
    UserInfoBase userInfoBase;

}
