package com.war4.service.impl;

import com.war4.base.CutPage;
import com.war4.pojo.UserViewHistory;
import com.war4.repository.BaseRepository;
import com.war4.service.ArticleService;
import com.war4.service.UserViewHistoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.7.25 0025.
 */
@Service
public class UserViewHistoryServiceImpl implements UserViewHistoryService {
    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private ArticleService articleService;

    @Override
    @Transactional
    public int addViewHistory(UserViewHistory userViewHistory) {
        UserViewHistory uwh = queryByUserAndContent(userViewHistory.getUserId(), userViewHistory.getContentId());
        if (uwh == null) {
            //创建新的浏览历史
            uwh = new UserViewHistory();
            uwh.setUserId(userViewHistory.getUserId());
            uwh.setContentId(userViewHistory.getContentId());
            uwh.setCreated(new Date());
            uwh.setModule(userViewHistory.getModule());
            uwh.setRecentViewed(new Date());
            uwh.setViewCount(1);
            baseRepository.saveObj(uwh);
            return 1;
        }
        //更新浏览历史
        uwh.setViewCount(uwh.getViewCount() + 1);
        uwh.setRecentViewed(new Date());
        baseRepository.updateObj(uwh);
        return uwh.getViewCount();
    }

    @Override
    public UserViewHistory queryByUserAndContent(String userId, Long contentId) {
        String hql = "from UserViewHistory where userId = :userId and contentId = :contentId";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("contentId", contentId);
        return baseRepository.executeHql(hql, params);
    }

    @Override
    public CutPage<UserViewHistory> queryUserViewHistory(String userId, String module, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(150);
        hql.append("from UserViewHistory where userId = '").append(userId);
        if (StringUtils.isNotBlank(module)){
            hql.append("' and module = '").append(module);
        }
        hql.append("' order by recentViewed desc");
        CutPage<UserViewHistory> cutPage = baseRepository.queryCutPageData(hql.toString(), new CutPage<>(page, limit));
        for (UserViewHistory userViewHistory : cutPage.getiData()) {
            userViewHistory.setContent(articleService.selectArticleById(userViewHistory.getContentId(),null));
        }
        return cutPage;
    }
}
