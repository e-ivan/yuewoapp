package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/12/19.
 */

@Entity
@Table(name = "dating_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class DatingSetting {

    @Id
    private String fkUserId;

    @Column(name = "fk_gift_id")
    private String fkGiftId;

    @Column(name = "is_accept")
    private Integer isAccept;

    @Transient
    private Gift gift;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间

    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记

}
