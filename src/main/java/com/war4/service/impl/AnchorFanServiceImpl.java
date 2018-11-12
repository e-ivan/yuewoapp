package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.AnchorFan;
import com.war4.pojo.UserInfoBase;
import com.war4.repository.BaseRepository;
import com.war4.service.AnchorFanService;
import com.war4.service.AssembleService;
import com.war4.service.UserInfoBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/13.
 */
@Service
public class AnchorFanServiceImpl implements AnchorFanService{

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private UserInfoBaseService userInfoBaseService;

    @Override
    @Transactional
    public void addAnchorFan(String userId, String anchorId) {

        if(checkIsMyAttention(userId, anchorId)!=null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"您已经关注了该主播！");
        }
        AnchorFan anchorFan = new AnchorFan();
        anchorFan.setId(UUID.randomUUID().toString());
        anchorFan.setAnchorUserId(anchorId);
        anchorFan.setFanUserId(userId);
        UserInfoBase userInfoBase=baseRepository.getObjById(UserInfoBase.class,anchorId);
        userInfoBase.setFanNum(userInfoBase.getFanNum()+1);
        baseRepository.updateObj(userInfoBase);
        baseRepository.saveObj(anchorFan);
    }

    @Override
    public AnchorFan checkIsMyAttention(String userId, String anchorId) {
        if(userId==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"当前用户Id不能为空");
        }
        if(anchorId==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"关注的主播Id不能为空");
        }
        String hql = baseRepository.getBaseHqlByClass(AnchorFan.class);
        hql += " and fanUserId ='"+userId+"' ";
        hql += " and anchorUserId ='"+anchorId+"' ";
        AnchorFan anchorFan = baseRepository.queryUniqueData(hql);
        return anchorFan;
    }

    @Override
    @Transactional
    public void deleteAnchorFan(String userId, String anchorId) {
        AnchorFan anchorFan = checkIsMyAttention(userId, anchorId);
        if(anchorFan==null){
           return;
        }
        UserInfoBase userInfoBase=baseRepository.getObjById(UserInfoBase.class,anchorId);
        userInfoBase.setFanNum(userInfoBase.getFanNum()-1);
        baseRepository.updateObj(userInfoBase);
        baseRepository.logicDelete(anchorFan);
    }

    @Override
    public CutPage<AnchorFan> queryMyFan(String anchorId, Integer page, Integer limit) {
        if(anchorId==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"当前用户Id不能为空");
        }
        CutPage<AnchorFan> cutPage = new CutPage<>(page,limit);
        String hql = baseRepository.getBaseHqlByClass(AnchorFan.class);
        hql += " and anchorUserId ='"+anchorId+"' ";
        hql += " order by createTime desc";
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(AnchorFan anchorFan :cutPage.getiData()){
            anchorFan.setUserFan(userInfoBaseService.getUserById(anchorFan.getFanUserId()));
        }
        return cutPage;
    }

    @Override
    public CutPage<AnchorFan> queryMyAnchor(String userId, Integer page, Integer limit) {
        if(userId==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"当前用户Id不能为空");
        }
        CutPage<AnchorFan> cutPage = new CutPage<>(page,limit);
        String hql = baseRepository.getBaseHqlByClass(AnchorFan.class);
        hql += " and fanUserId ='"+userId+"' ";
        hql += " order by createTime desc";
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(AnchorFan anchorFan :cutPage.getiData()){
            anchorFan.setUserFan(userInfoBaseService.getUserById(anchorFan.getAnchorUserId()));
        }
        return cutPage;
    }


}
