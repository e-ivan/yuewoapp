package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommentSystemState;
import com.war4.enums.CommentSystemType;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.CommentSystem;
import com.war4.service.CommentSystemService;
import com.war4.service.UserInfoBaseService;
import com.war4.util.UserContext;
import com.war4.vo.SimpleUserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by E_Iva on 2018.4.11.0011.
 */
@Service
public class CommentSystemServiceImpl extends BaseService implements CommentSystemService {
    @Autowired
    private UserInfoBaseService userInfoBaseService;
    @Override
    @Transactional
    public void addComment(CommentSystem comment) {
        if (StringUtils.isBlank(comment.getContent())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "评论内容不能为空");
        }
        //获取评论所属对象类型
        CommentSystemType type = CommentSystemType.getByCode(comment.getType());
        if (type == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "评论类型不存在");
        }
        //对象添加评论次数
        int i = this.addTypeCommentCount(type,comment.getObjectId());
        if (i < 1){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "评论对象不存在");
        }
        CommentSystem commentSystem = new CommentSystem();
        commentSystem.setUserId(comment.getUserId());
        commentSystem.setObjectId(comment.getObjectId());
        commentSystem.setContent(comment.getContent());
        commentSystem.setState(CommentSystemState.NORMAL.getCode());
        if (comment.getParentId() != null) {//回复评论
            CommentSystem replyComment = baseRepository.getObjById(CommentSystem.class, comment.getParentId());
            if (replyComment == null) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "回复评论不存在");
            }
            /*if (user.getUid().equals(comment.getUid())) {
                throw new BusinessException(CommonErrorResultEnum.OWN2OWN);
            }*/
            if (replyComment.getParentId() == null) {//第一级回复
                commentSystem.setParentId(replyComment.getId());
                //根评论回复数量增加
                this.addCommentReplyCount(replyComment.getId());
            } else {//第二级回复
                commentSystem.setReplyUserId(replyComment.getUserId());
                commentSystem.setParentId(replyComment.getParentId());
                //根评论回复数量增加
                this.addCommentReplyCount(replyComment.getParentId());
                //被评论回复数量增加
                this.addCommentReplyCount(comment.getParentId());
            }
        }
        baseRepository.saveObj(commentSystem);
    }

    @Override
    public List<CommentSystem> queryComment(Integer type, String objectId,Long parentId) {
        StringBuilder hql = new StringBuilder(150);
        hql.append("from CommentSystem where state = :state and parentId ");
        hql.append(parentId == null ? " is null " : " = :parentId ");
        hql.append(" and objectId = :objectId order by created");
        Map<String, Object> map = baseRepository.paramMap();
        map.put("state", CommentSystemState.NORMAL.getCode());
        map.put("parentId", parentId);
        map.put("objectId",objectId);
        List<CommentSystem> comments = baseRepository.queryHqlResult(hql, map, 0, CutPage.MAX_COUNT);
        Set<String> userSet = new HashSet<>();
        comments.forEach(c ->{
            if (c.getUserId() != null) {
                userSet.add(c.getUserId());
            }
            if (c.getReplyUserId() != null) {
                userSet.add(c.getReplyUserId());
            }
        });
        Map<String, SimpleUserVO> userMap = userInfoBaseService.querySimpleUserMap(userSet);
        comments.forEach(c -> {
            SimpleUserVO user = userMap.get(c.getUserId());
            if (user != null) {
                c.setUser(user);
            }
            SimpleUserVO replyUser = userMap.get(c.getReplyUserId());
            if (replyUser != null) {
                c.setReplyUser(replyUser);
            }
        });
        return comments;
    }

    @Override
    @Transactional
    public void updateCommentState(Long commentId, boolean state) {
        CommentSystem commentSystem = baseRepository.getObjById(CommentSystem.class, commentId);
        if (commentSystem != null && StringUtils.equals(UserContext.getUserId(),commentSystem.getUserId())) {
            commentSystem.setState(state ? CommentSystemState.NORMAL.getCode() : CommentSystemState.DELETE.getCode());
            baseRepository.updateObj(commentSystem);
        }
    }

    /**
     * 添加类型对象评论次数
     */
    private int addTypeCommentCount(CommentSystemType type, String id) {
        String hql = "update " + type.getClzName() + " set commentCount = commentCount + 1 where id = :id";
        Map<String, Object> map = baseRepository.paramMap();
        Serializable serializable = baseRepository.objectIdOfType(type.getClz(), id);
        map.put("id",serializable);
        return baseRepository.executeHql(hql, map);
    }

    /**
     * 添加评论回复次数
     */
    private void addCommentReplyCount(Long id) {
        String hql = "update CommentSystem set replyCount = replyCount + 1 where id = :id";
        Map<String, Object> map = baseRepository.paramMap();
        map.put("id", id);
        baseRepository.executeHql(hql, map);
    }

}
