package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.NearbyDating;
import com.war4.pojo.NearbyDatingApplyItem;
import com.war4.vo.NearbyDatingParamVO;

/**
 * 附近有约服务
 * Created by E_Iva on 2018.3.23.0023.
 */
public interface NearbyDatingService {
    void createNearbyDating(NearbyDatingParamVO vo);
    void createNearbyDatingApplyItem(Long nId);
    void cancelNearbyDating(Long nId, String createUser);
    NearbyDating getNearbyDatingById(Long nId, String lng, String lat);

    /**
     * 根据位置信息查询附近有约
     */
    CutPage<NearbyDating> queryNearbyDatingByDistance(String lng, String lat, Integer page, Integer limit);

    CutPage<NearbyDating> queryAllNearbyDating(String keyword, Integer state, Integer page, Integer limit);

    CutPage<NearbyDatingApplyItem> queryNearbyDatingApplyItem(Long nId, Integer page, Integer limit);

    /**
     * 查询用户发布的附近有约
     */
    CutPage<NearbyDating> queryUserNearbyDating(String userId,boolean setUser, Integer page, Integer limit);

    /**
     * 查询用户报名的附近有约
     */
    CutPage<NearbyDating> queryUserApplyNearbyDating(String userId, Integer page, Integer limit);

}
