package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.UserViewHistory;

/**
 * 用户浏览历史服务
 * Created by hh on 2017.7.25 0025.
 */
public interface UserViewHistoryService {
    /**
     * 添加用户浏览历史
     * @param userViewHistory
     * @return 阅读次数
     */
    int addViewHistory(UserViewHistory userViewHistory);

    /**
     * 根据用户和文章获取浏览历史
     * @param userId
     * @param contentId
     */
    UserViewHistory queryByUserAndContent(String userId,Long contentId);

    /**
     * 查询用户的浏览历史
     * @param userId
     * @param module
     */
    CutPage<UserViewHistory> queryUserViewHistory(String userId, String module, Integer page, Integer limit);
}
