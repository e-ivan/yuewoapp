package com.war4.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by E_Iva on 2018.3.6.0006.
 */
@Getter@Setter
public class AuditParamVO {
    private Long id;
    private String remark;
    private boolean pass;
    private String auditorId;
}
