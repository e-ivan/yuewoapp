package com.war4.service;

/**
 * 收藏服务
 * Created by hh on 2017.7.14 0014.
 */
public interface CollectMappingService {
    /**
     * 收藏文章
     * @param userId    用户id
     * @param contentId 文章内容id
     * @return 返回文章总收藏数量
     */
    int collectArticle(String userId,String contentId);

}
