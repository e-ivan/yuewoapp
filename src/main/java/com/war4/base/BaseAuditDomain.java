package com.war4.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 认证基类
 * Created by hh on 2017.10.30 0030.
 */
@MappedSuperclass
@Getter@Setter
public class BaseAuditDomain {

    protected Integer status;

    protected String applierId;   //申请用户

    protected String auditorId;   //审核用户

    protected Date auditTime;     //审核时间

    protected String remark;      //审核留言

    protected Timestamp createTime;//申请时间

    @Version
    @JsonIgnore
    protected Integer version;
}
