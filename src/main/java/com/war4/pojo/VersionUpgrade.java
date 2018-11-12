package com.war4.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * App版本升级
 */
@Entity
@Table(name = "version_upgrade")
@DynamicInsert(true)
@DynamicUpdate(true)
@Getter@Setter
public class VersionUpgrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer versionCode;

    private String versionName;

    @JsonIgnore
    private Date created;

    private boolean must;

    private String apkUrl;

    @JsonIgnore
    private byte appType;

    private String upgradePoint;

}