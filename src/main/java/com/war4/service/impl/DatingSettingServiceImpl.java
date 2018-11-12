package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.DatingSettingIsAccept;
import com.war4.pojo.DatingSetting;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.DatingSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2016/12/19.
 */

@Service
public class DatingSettingServiceImpl implements DatingSettingService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public void addDatingSetting(String fkUserId, Integer isAccept, String fkGiftId) {
        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空");
        }
        if(isAccept==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"是否接受约会设置不能为空");
        }
        if(DatingSettingIsAccept.getByCode(isAccept)==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"是否接受约会设置不存在");
        }
        DatingSetting datingSetting = new DatingSetting();
        datingSetting.setFkUserId(fkUserId);
        datingSetting.setIsAccept(isAccept);
        datingSetting.setFkGiftId(fkGiftId);
        baseRepository.saveObj(datingSetting);
    }

    @Override
    @Transactional
    public void updateDatingSetting(String fkUserId, Integer isAccept, String fkGiftId) {
        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空");
        }
        if(isAccept==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"是否接受约会设置不能为空");
        }
        if(fkGiftId==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"约会礼物不能为空");
        }
        if(DatingSettingIsAccept.getByCode(isAccept)==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"是否接受约会设置不存在");
        }
        DatingSetting datingSetting = baseRepository.getObjById(DatingSetting.class,fkUserId);
        if(datingSetting==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"是约会设置不存在,请重新设置！");
        }
        if(DatingSettingIsAccept.getByCode(isAccept)==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"是否接受约会设置不存在");
        }
        datingSetting.setFkGiftId(fkGiftId);
        datingSetting.setIsAccept(isAccept);
        baseRepository.updateObj(datingSetting);
    }

    @Override
    public DatingSetting queryDatingSettingByUserId(String fkUserId) {
        DatingSetting datingSetting = baseRepository.getObjById(DatingSetting.class,fkUserId);
        if(datingSetting!=null){
            datingSetting = assembleService.assembleObject(datingSetting);
        }
        return datingSetting;
    }
}
