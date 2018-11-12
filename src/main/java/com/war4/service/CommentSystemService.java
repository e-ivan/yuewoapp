package com.war4.service;

import com.war4.pojo.CommentSystem;

import java.util.List;

/**
 * 评论系统服务
 * Created by E_Iva on 2018.4.11.0011.
 */
public interface CommentSystemService {
    void addComment(CommentSystem comment);

    List<CommentSystem> queryComment(Integer type,String objectId,Long parentId);

    /**
     * 更新评论状态
     */
    void updateCommentState(Long commentId, boolean state);
}
