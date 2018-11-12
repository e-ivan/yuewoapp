package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.AnchorFan;

/**
 * Created by Administrator on 2016/12/13.
 */
public interface AnchorFanService {
    void addAnchorFan(String userId,String anchorId);
    AnchorFan checkIsMyAttention(String userId, String anchorId);
    void deleteAnchorFan(String userId, String anchorId);
    CutPage<AnchorFan> queryMyFan(String anchorId,Integer page,Integer limit);
    CutPage<AnchorFan> queryMyAnchor(String userId,Integer page,Integer limit);
}
