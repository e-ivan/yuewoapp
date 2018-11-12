package com.war4.service;


import com.war4.vo.AuditParamVO;

/**
 * 竞拍活动服务
 * Created by hh on 2017.10.30 0030.
 */
public interface AuditService {

    void updateAuthentication(AuditParamVO vo);

    void updateUserVideo(AuditParamVO vo);

    void updateInternetStar(AuditParamVO vo);

}
