package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.ResumeWorItem;

/**
 * Created by Administrator on 2016/12/21.
 */
public interface ResumeWorItemService {
    ResumeWorItem addResumeWorItem(ResumeWorItem resumeWorItem);
    ResumeWorItem queryResumeWorItemDetailById(String itemId);
    CutPage<ResumeWorItem> queryResumeWorItemDirectorListByType(String userId,Integer page,Integer limit);
    CutPage<ResumeWorItem> queryResumeWorItemSeceenwriterListByType(String userId,Integer page,Integer limit);
    CutPage<ResumeWorItem> queryResumeWorItemPerformeristByType(String userId,Integer page,Integer limit);
    Integer queryResumeWorItemSum(String userId,String workType);
    void deleteResumeWorItem(String itemId);
}
