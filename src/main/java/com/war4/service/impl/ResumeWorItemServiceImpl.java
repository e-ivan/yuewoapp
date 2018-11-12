package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.ResumeWorkType;
import com.war4.pojo.ResumeWorItem;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.ResumeWorItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/21.
 */
@Service
public class ResumeWorItemServiceImpl implements ResumeWorItemService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public ResumeWorItem addResumeWorItem(ResumeWorItem resumeWorItem) {
        if(resumeWorItem.getFkUserId()==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"fkUserId不能为空！");
        }
        if(resumeWorItem.getTitle()==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"标题不能为空！！");
        }
        if(resumeWorItem.getIntro()==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"简介不能为空！！");
        }
        if(resumeWorItem.getWorkType()==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"作品类型不能为空！！");
        }
        if(ResumeWorkType.getByValue(resumeWorItem.getWorkType())==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"作品类型不存在！！");
        }
        resumeWorItem.setId(UUID.randomUUID().toString());
        baseRepository.saveObj(resumeWorItem);
        return resumeWorItem;
    }

    @Override
    public ResumeWorItem queryResumeWorItemDetailById(String itemId) {
        ResumeWorItem resumeWorItem = baseRepository.getObjById(ResumeWorItem.class,itemId);
        if(resumeWorItem!=null){
            resumeWorItem = assembleService.assembleObject(resumeWorItem);
        }
        return resumeWorItem;
    }

    @Override
    public CutPage<ResumeWorItem> queryResumeWorItemDirectorListByType(String userId,Integer page,Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(ResumeWorItem.class);
        hql += " and fkUserId ='"+userId+"' ";
        hql += " and workType ='"+ ResumeWorkType.DIRECTOR.getCode()+"'";
        hql += " order by createTime desc";
        CutPage<ResumeWorItem> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for (ResumeWorItem itme :cutPage.getiData()){
            itme = assembleService.assembleObject(itme);
        }
        return cutPage;
    }

    @Override
    public CutPage<ResumeWorItem> queryResumeWorItemPerformeristByType(String userId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(ResumeWorItem.class);
        hql += " and fkUserId ='"+userId+"' ";
        hql += " and workType ='"+ ResumeWorkType.PERFORMER.getCode()+"'";
        hql += " order by createTime desc";
        CutPage<ResumeWorItem> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for (ResumeWorItem itme :cutPage.getiData()){
            itme = assembleService.assembleObject(itme);
        }
        return cutPage;
    }

    @Override
    public CutPage<ResumeWorItem> queryResumeWorItemSeceenwriterListByType(String userId,Integer page,Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(ResumeWorItem.class);
        hql += " and fkUserId ='"+userId+"' ";
        hql += " and workType ='"+ ResumeWorkType.SECEENWRITER.getCode()+"'";
        hql += " order by createTime desc";
        CutPage<ResumeWorItem> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for (ResumeWorItem itme :cutPage.getiData()){
            itme = assembleService.assembleObject(itme);
        }
        return cutPage;
    }

    @Override
    public Integer queryResumeWorItemSum(String userId, String workType) {
        if(workType==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"作品类型不能为空！！");
        }
        if(ResumeWorkType.getByValue(workType)==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"作品类型不存在！！");
        }
        String hql = baseRepository.getBaseHqlByClass(ResumeWorItem.class);
        hql += " and fkUserId ='"+userId+"' ";
        hql += " and workType ='"+ workType +"' ";
        Integer count = baseRepository.getTotalCount(hql).intValue();

        return count;
    }


    @Override
    @Transactional
    public void deleteResumeWorItem(String itemId) {
        ResumeWorItem resumeWorItem = baseRepository.getObjById(ResumeWorItem.class,itemId);
        if(resumeWorItem==null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"不存在的作品详情！");
        }
        baseRepository.logicDelete(resumeWorItem);
    }
}
