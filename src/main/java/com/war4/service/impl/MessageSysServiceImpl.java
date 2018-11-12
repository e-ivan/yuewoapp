package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.base.SystemParameters;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.MessageSys;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.MessageSysService;
import com.war4.service.PushPayloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/1/9.
 */
@Service
public class MessageSysServiceImpl implements MessageSysService {

    public Logger logger = Logger.getLogger(MessageSysServiceImpl.class.getName());

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private PushPayloadService pushPayloadService;


    @Override
    @Transactional
    public void addSyaMessageSys(MessageSys messageSys) {
        if(messageSys.getContent()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"推送内容不能为空！");
        }
        messageSys.setFkUserId(SystemParameters.SYSTEM_USER_ID);
        messageSys.setId(UUID.randomUUID().toString());

        baseRepository.saveObj(messageSys);
        try {
            pushPayloadService.addAllUserPushPayload(PropertiesConfig.getJpushAppMasterSecret(), PropertiesConfig.getJpushAppkey(), messageSys.getContent(), null);
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    @Override
    @Transactional
    public void addPersonMessageSys(MessageSys messageSys) {
        if(messageSys.getContent()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"推送内容不能为空！");
        }
        if(messageSys.getFkUserId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"推送用户id不能为空！");
        }
        messageSys.setId(UUID.randomUUID().toString());
        baseRepository.saveObj(messageSys);

        try {
            pushPayloadService.addPushPayloadForUserName(PropertiesConfig.getJpushAppMasterSecret(), PropertiesConfig.getJpushAppkey(), messageSys.getFkUserId(), messageSys.getContent());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    @Override
    public CutPage<MessageSys> queryUserMessageSys(String userId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(MessageSys.class);
        hql += " and ( fkUserId = '"+userId+"' or  fkUserId ='"+SystemParameters.SYSTEM_USER_ID +"' ) ";
        CutPage<MessageSys> cutPage = baseRepository.queryCutPageData(hql,new CutPage<>(page,limit));
        return cutPage;
    }

    @Override
    public CutPage<MessageSys> querySysMessageSys( Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(MessageSys.class);
        hql += " and  fkUserId ='"+SystemParameters.SYSTEM_USER_ID +"'  ";
        CutPage<MessageSys> cutPage = baseRepository.queryCutPageData(hql,new CutPage<>(page,limit));
        return cutPage;
    }
}
