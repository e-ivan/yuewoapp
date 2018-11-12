package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.RecruitApply;

/**
 * Created by Administrator on 2016/12/22.
 */
public interface RecruitApplyService {
    RecruitApply saveRecruitApply(RecruitApply recruitApply);
    RecruitApply queryRecruitApplyById(String applyId);
    void deleteRecruitApplyById(String applyId);
    CutPage<RecruitApply> queryRecruitApplyList(String recruitInfoId,Integer page,Integer limit);
}
