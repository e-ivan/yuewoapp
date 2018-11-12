package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CollectState;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.PushEvent;
import com.war4.listener.event.ViewHistoryEvent;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.ArticleService;
import com.war4.service.UserCorrelativeService;
import com.war4.vo.PushMsgVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hh on 2017.7.8 0008.
 */
@Service
public class ArticleServiceImpl implements ArticleService, ApplicationContextAware {
    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private UserCorrelativeService correlativeService;

    private ApplicationContext ac;

    @Override
    public CutPage<Content> queryArticlesByModule(String module, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(300);
        Map<String,Object> params = new HashMap<>();
        hql.append("select new com.war4.vo.ArticleVO(id,covers,title,summary,type,commentCount,voteUp,linkTo) from Content where status = 2 ");
        if (StringUtils.isNotEmpty(module)) {
            if ("quality".equals(module)){//精选
                hql.append(" and isQuality = 1 ");
            }else {
                params.put("module", module);
                hql.append(" and module = :module ");
            }
        }
        hql.append(" order by orderNumber desc,modified desc");
        return baseRepository.executeHql(hql.toString(), params, page, limit);
    }

    @Override
    public Content selectArticleById(Long id, String userId) {
        String hql = " from Content c where c.status = 2 and c.id = " + id;
        Content content = baseRepository.queryUniqueData(hql);
        if (content == null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"内容不存在");
        }
        //阅读次数+1
        addContentCountByName(id, "viewCount");
        if (!content.getModule().equals("") && StringUtils.isNotBlank(userId)) {//需要记录阅读次数的模块,且不是用户查看
            //发送历史监听事件
            ac.publishEvent(new ViewHistoryEvent(new UserViewHistory(userId,id,content.getModule())));
        }
        return content;
    }

    @Override
    @Transactional
    public void addContentCountByName(Long contentId, String countName) {
        String hql = "update Content set " + countName + " = " + countName + " + 1,commentTime = :time  where id = :id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", contentId);
        paramMap.put("time", new Date());
        baseRepository.executeHql(hql, paramMap);
    }

    @Override
    public void addCommentCountByName(Long commentId, String countName) {
        String hql = "update Comment set " + countName + " = " + countName + " + 1  where id = :id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", commentId);
        baseRepository.executeHql(hql, paramMap);
    }


    @Override
    @Transactional
    public void addComment(Comment comment) {
        if (comment.getContentId() == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "contentId不能为空");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, comment.getUserId());
        if (user == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "用户不存在！");
        }
        if (StringUtils.isBlank(comment.getText())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "text不能为空");
        }
        if (StringUtils.isBlank(comment.getAuthor())) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "author不能为空");
        }
        Long parentId = comment.getParentId();
        Comment c = new Comment();
        c.setStatus(Comment.NORMAL);
        c.setAgent(comment.getAgent());
        c.setAuthor(comment.getAuthor());
        c.setContentId(comment.getContentId());
        c.setContentModule(comment.getContentModule());
        c.setCreated(new Date());
        c.setIp(comment.getIp());
        c.setParentId(parentId);
        c.setText(comment.getText());
        c.setUserId(comment.getUserId());
        c.setLng(comment.getLng());
        c.setLat(comment.getLat());
        c.setUserPhotoHead(user.getUserPhotoHead());
        //评论次数+1
        addContentCountByName(comment.getContentId(), "commentCount");
        Comment parentComment = selectCommentById(parentId);
        if (parentComment != null) {
            String parentUserId = parentComment.getUserId();
            if (comment.getUserId().equals(parentUserId)) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能自言自语哦！");
            }
            //评论的序号
            c.setOrderNumber(parentComment.getCommentCount() + 1);
            //评论的回复次数+1
            addCommentCountByName(parentId, "commentCount");

            //如果是回复评论，则通知对方
            PushMsgVO pmv = new PushMsgVO(parentUserId,comment.getUserId(),"有人回复了你的评论!",comment.getText(),comment.getId(),MessageLogType.ARTICLE_COMMENT);
            ac.publishEvent(new PushEvent(pmv));//发送事件
        }
        baseRepository.saveObj(c);
    }

    @Override
    public Comment selectCommentById(Long commentId) {
        return baseRepository.queryUniqueData(" from Comment where status = 0 and id = " + commentId + " ");
    }

    @Override
    @Transactional
    public CutPage<Comment> queryArticleComments(Long contentId, String orderBy, Integer page, Integer limit) {
        StringBuilder sql = new StringBuilder(700);
        sql.append("SELECT c.id,c.parent_id,c.content_id,c.comment_count,c.content_module,c.order_number,c.user_id,");
        sql.append("u.user_photo_head,u.nick_name as author,c.ip,c.agent,c.created,c.status,c.vote_up,c.vote_down,c.lng,c.lat,c.text ");
        sql.append(" FROM comment c LEFT JOIN user_info_base u ON (c.user_id = u.id) WHERE c.status = 0 and c.content_id = :contentId");
        Map<String,Object> map = new HashMap<>();
        map.put("contentId",contentId);
        CutPage<Comment> cutPage = baseRepository.executeSql(addCommentOrderBy(sql.toString(),orderBy), map,Comment.class, page, limit);
        for (Comment c : cutPage.getiData()) {
            if (c.getParentId() != null) {//引用回复评论
                c.setParent(selectCommentById(c.getParentId()));
            }
        }
        return cutPage;
    }

    @Override
    public CutPage<Comment> queryCommentsInComment(Long commentId, String orderBy, Integer page, Integer limit) {
        StringBuilder sql = new StringBuilder(700);
        sql.append("SELECT c.id,c.parent_id,c.content_id,c.comment_count,c.content_module,c.order_number,c.user_id,");
        sql.append("u.user_photo_head,u.nick_name as author,c.ip,c.agent,c.created,c.status,c.vote_up,c.vote_down,c.lng,c.lat,c.text ");
        sql.append(" FROM comment c LEFT JOIN user_info_base u ON (c.user_id = u.id) WHERE c.status = 0 and c.parent_id = :commentId");
        Map<String,Object> map = new HashMap<>();
        map.put("commentId",commentId);
        CutPage<Comment> cutPage = baseRepository.executeSql(addCommentOrderBy(sql.toString(),orderBy), map,Comment.class, page, limit);
        return cutPage;
    }

    @Override
    @Transactional
    public void addArticleVote(Long contentId, String userId) {
        if (contentId == null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"文章id不能为空！");
        }
        if (StringUtils.isBlank(userId)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空");
        }
        ArticleVoteUser avu = checkArticleVoteByUser(contentId, userId);
        if (avu != null){
            if (avu.getState() == CollectState.COLLECTION.getValue()){
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "你已点赞！");
            }else {
                avu.setOperateTime(new Date());
                avu.setState(CollectState.COLLECTION.getValue());
                avu.setCount(avu.getCount() + 1);
                baseRepository.updateObj(avu);
                //添加点赞数量
                addContentCountByName(contentId,"voteUp");
                addContentCountByName(contentId,"realVote");
                //用户文章点赞+1
                correlativeService.addCount(UserCorrelative.ARTICLE_VOTES,userId);
                return;
            }
        }
        avu = new ArticleVoteUser();
        avu.setCreated(new Date());
        avu.setContentId(contentId);
        avu.setUserId(userId);
        avu.setOperateTime(new Date());
        avu.setCount(1);
        avu.setState(CollectState.COLLECTION.getValue());

        baseRepository.saveObj(avu);
        //添加点赞数量
        addContentCountByName(contentId,"voteUp");
        addContentCountByName(contentId,"realVote");
        //用户文章点赞+1
        correlativeService.addCount(UserCorrelative.ARTICLE_VOTES,userId);
    }

    @Override
    @Transactional
    public void cancelArticleVote(Long contentId, String userId) {
        if (contentId == null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"文章id不能为空！");
        }
        if (StringUtils.isBlank(userId)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空");
        }
        ArticleVoteUser avu = checkArticleVoteByUser(contentId, userId);
        if (avu != null && avu.getState() != CollectState.CANCEL_COLLECT.getValue()){
            avu.setState(CollectState.CANCEL_COLLECT.getValue());
            avu.setOperateTime(new Date());
            baseRepository.updateObj(avu);
            //减少点赞数量
            String hql = "update Content set voteUp = voteUp - 1,realVote = realVote - 1 where id = " + contentId;
            baseRepository.executeHql(hql,null);
            //用户文章点赞-1
            correlativeService.reduceCount(UserCorrelative.ARTICLE_VOTES,userId);
        }
    }

    @Override
    public ArticleVoteUser checkArticleVoteByUser(Long contentId, String userId) {
        if (contentId == null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"文章id不能为空！");
        }
        if (StringUtils.isBlank(userId)){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户id不能为空");
        }
        String hql = "from ArticleVoteUser where contentId = :contentId and userId = :userId";
        Map<String,Object> param = new HashMap<>();
        param.put("contentId",contentId);
        param.put("userId",userId);
        return baseRepository.executeHql(hql, param);
    }

    private String addCommentOrderBy(String sql, String orderBy) {
        if (StringUtils.isNotBlank(orderBy)) {//默认按最新评论在前
            if ("commentup".equals(orderBy.toLowerCase())) {//评论数量多的在前
                sql += " ORDER BY c.comment_count desc,c.created desc";
            } else if ("commentdown".equals(orderBy.toLowerCase())) {//评论数量少的在前
                sql += " ORDER BY c.comment_count asc";
            } else if ("timedown".equals(orderBy.toLowerCase())) {//时间正序
                sql += " ORDER BY c.created asc";
            } else {
                sql += " ORDER BY c.created desc";
            }
        } else {
            sql += " ORDER BY c.created desc";
        }
        return sql;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
