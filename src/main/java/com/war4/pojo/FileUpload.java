package com.war4.pojo;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by dly on 2016/8/25.
 */
@Entity
@Table(name = "file")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class FileUpload {
    @Id
    private String id;

    @Column(name = "file_type")
    private String fileType;
    @Column(name = "purpose")
    private String purpose;
    @Column(name = "fk_purpose_id")
    private String fkPurposeId;
    @Column(name = "location")
    private String location;

    @Column(name = "create_time")
    private Timestamp createTime;                          //创建时间
    @Column(name = "del_flag")
    private Integer delFlag;                                //删除标记
}
