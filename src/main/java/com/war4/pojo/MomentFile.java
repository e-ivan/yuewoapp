package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * 朋友圈文件
 */
@Entity
@Table(name = "moment_file")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter
@Setter
public class MomentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonIgnore
    private Long momentId;

    private String path;

    @JsonIgnore
    private String userId;

    @Column(name = "size_h")
    private Integer sizeH;

    @Column(name = "size_w")
    private Integer sizeW;

    @JsonIgnore
    private Date created;

    @JsonIgnore
    private String type;

    @JsonIgnore
    private String mime;
}