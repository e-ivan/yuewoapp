package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.ArticleVoteUser;
import com.war4.pojo.Comment;
import com.war4.pojo.Content;

/**
 * 文章服务
 * Created by hh on 2017.7.8 0008.
 */
public interface ArticleService {
    CutPage<Content> queryArticlesByModule(String module, Integer page, Integer limit);
    Content selectArticleById(Long id,String userId);

    /**
     * 向文章指定计算类型+1条记录
     * @param countName
     */
    void addContentCountByName(Long contentId,String countName);
    /**
     * 向评论指定计算类型+1条记录
     * @param countName
     */
    void addCommentCountByName(Long commentId,String countName);

    /**
     * 添加评论
     */
    void addComment(Comment comment);

    /**
     * 通过id查询某条评论
     * @param commentId
     */
    Comment selectCommentById(Long commentId);

    /**
     * 获取文章所有评论
     * @param contentId
     * @param orderBy   //排序依据
     */
    CutPage<Comment> queryArticleComments(Long contentId,String orderBy,Integer page,Integer limit);

    /**
     * 获取评论中所有回复
     * @param commentId
     * @param orderBy   //排序依据
     */
    CutPage<Comment> queryCommentsInComment(Long commentId,String orderBy,Integer page,Integer limit);

    /**
     * 文章点赞
     * @param contentId
     * @param userId
     */
    void addArticleVote(Long contentId,String userId);

    /**
     * 取消文章点赞
     * @param contentId
     * @param userId
     */
    void cancelArticleVote(Long contentId,String userId);

    /**
     * 查询点赞记录
     * @param contentId
     * @param userId
     */
    ArticleVoteUser checkArticleVoteByUser(Long contentId,String userId);

}
