package com.war4.service;

import com.war4.pojo.DatingSetting;

/**
 * Created by Administrator on 2016/12/19.
 */
public interface DatingSettingService {
    void addDatingSetting(String fkUserId,Integer isAccept,String fkGiftId);
    void updateDatingSetting(String fkUserId,Integer isAccept,String fkGiftId );
    DatingSetting queryDatingSettingByUserId(String fkUserId);
}
