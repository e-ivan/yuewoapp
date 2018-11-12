package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.enums.CollectType;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.CollectMapping;
import com.war4.pojo.Content;
import com.war4.repository.BaseRepository;
import com.war4.service.ArticleService;
import com.war4.service.CollectMappingService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *
 * Created by hh on 2017.7.14 0014.
 */
@Service
public class CollectMappingServiceImpl implements CollectMappingService {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public int collectArticle(String userId, String contentId) {
        Content content = articleService.selectArticleById(Long.parseLong(contentId), null);
        if (userId != null && content != null) {
            CollectMapping cm = new CollectMapping();
            cm.setState(CollectMapping.COLLECTION);
            cm.setCollectTime(new Date());
            cm.setUserId(userId);
            cm.setObjectId(contentId);
            //添加封面中第一张图片
            cm.setPicture((String) JSONArray.fromObject(content.getCovers()).getJSONObject(0).get("path"));
            cm.setSummary(content.getSummary());
            cm.setType(CollectType.ARTICLE.getValue());
            //保存收藏
            baseRepository.saveObj(cm);
            //添加文章收藏次数
            int collectCount = content.getCollectCount() + 1;
            content.setCollectCount(collectCount);
            baseRepository.updateObj(content);
            return collectCount;
        }
        throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"收藏失败");
    }


}
