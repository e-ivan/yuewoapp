package com.war4.service.impl;

import com.alibaba.fastjson.JSON;
import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CollectState;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.MessageLogType;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.*;
import com.war4.service.*;
import com.war4.vo.MomentFileVO;
import com.war4.vo.PushMsgVO;
import com.war4.vo.SimpleUserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hh on 2017.7.15 0015.
 */
@Service
public class MomentServiceImpl extends BaseService implements MomentService {
    @Autowired
    private AssembleService assembleService;
    @Autowired
    private ChatFriendService chatFriendService;
    @Autowired
    private UserCorrelativeService correlativeService;
    @Autowired
    private UserInfoBaseService userInfoBaseService;

    @Override
    @Transactional
    public void saveMoment(MultipartFile[] files, Moment moment) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, moment.getUserId());
        if (user == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "用户不存在");
        }
        Moment m = new Moment();
        m.setContent(moment.getContent());
        m.setCreated(new Date());
        m.setUserId(moment.getUserId());
        m.setLng(user.getX());
        m.setLat(user.getY());
        m.setIp(moment.getIp());
        m.setState(Moment.NORMAL);
        m.setType(files.length > 0 ? 1 : 0);
        baseRepository.saveObj(m);
        try {
            List<MomentFileVO> list = fileService.saveMomentFiles(files, m.getId(), m.getUserId());
            m.setImage(JSON.toJSONString(list));
            m.setImageCount(files.length);
            baseRepository.updateObj(m);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "图片上传失败！");
        }
        //用户朋友圈+1
        correlativeService.addCount(UserCorrelative.MOMENTS, moment.getUserId());
    }

    @Override
    public CutPage<Moment> queryFriendMoments(String userId, boolean yourself, Integer page, Integer limit) {
        if (StringUtils.isBlank(userId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "userId不能为空");
        }
        Map<String, Object> param = new HashMap<>();
        StringBuilder hql = new StringBuilder(300);
        hql.append("from Moment where state = :state and (userId in (select fkFriendUserId from ChatFriend where delFlag = 0 and fkUserId = :userId)");
        if (yourself) {
            hql.append(" or userId = :userId ");
        }
        hql.append(") order by created desc");
        param.put("state", Moment.NORMAL);
        param.put("userId", userId);
        CutPage<Moment> cutPage = baseRepository.executeHql(hql, param, page, limit);
        this.setMomentsUser(cutPage.getiData());
        this.assembleMoment(cutPage.getiData(), userId);
        return cutPage;
    }

    @Override
    public CutPage<Moment> queryMyMoment(String userId, Integer page, Integer limit) {
        if (StringUtils.isBlank(userId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "userId不能为空");
        }
        String hql = "from Moment where state = :state and userId = :userId order by created desc";
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("state", Moment.NORMAL);
        CutPage<Moment> cutPage = baseRepository.executeHql(hql, param, page, limit);
        List<Moment> moments = cutPage.getiData();
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user != null) {
            moments.forEach(m -> {
                m.setNickname(user.getNickName());
                m.setHeadImg(user.getUserPhotoHead());
            });
        }
        this.assembleMoment(moments, userId);
        return cutPage;
    }

    private void assembleMoment(Collection<Moment> moments, String userId) {
        //获取所有朋友圈id
        List<Long> momentIds = moments.stream().map(Moment::getId).collect(Collectors.toList());
        //获取评论
        Map<Long, List<MomentComment>> commentsMap = this.queryMomentCommentsMap(momentIds);
        Map<Long, List<MomentVoteUser>> votesMap = this.queryMomentVotesMap(momentIds);
        moments.forEach(m -> {
            List<MomentComment> comments = commentsMap.get(m.getId());
            if (comments != null && comments.size() > 0) {
                m.setComments(comments);
            }
            List<MomentVoteUser> votes = votesMap.get(m.getId());
            //查看是否有点赞
            if (votes != null && votes.size() > 0) {
                //查询是否有当前用户点赞
                m.setVote(votes.stream().anyMatch(mv -> StringUtils.equals(userId, mv.getUserId())));
                m.setVoteUsers(votes);
            }
        });

    }

    @Override
    public Moment getMomentById(Long momentId, String userId) {
        Moment moment = baseRepository.getObjById(Moment.class, momentId);
        if (moment == null || Objects.equals(moment.getState(), Moment.DELETE)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "朋友圈不存在！");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, moment.getUserId());
        if (user != null) {
            moment.setHeadImg(user.getUserPhotoHead());
            moment.setNickname(user.getNickName());
        }
        this.assembleMoment(Collections.singleton(moment), userId);
        return moment;
    }

    @Override
    public void updateMomentState(String userId, Long momentId, boolean state) {
        String hql = "update Moment set state = :state where userId = :userId and id = :momentId";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("momentId", momentId);
        paramMap.put("state", state ? Moment.NORMAL : Moment.DELETE);
        int i = baseRepository.executeHql(hql, paramMap);
        if (i <= 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, state ? "恢复失败" : "删除失败！");
        }
        //用户朋友圈-1
        if (state) {
            correlativeService.addCount(UserCorrelative.MOMENTS, userId);
        }else {
            correlativeService.reduceCount(UserCorrelative.MOMENTS, userId);
        }
    }

    @Override
    @Transactional
    public MomentComment addMomentComment(MomentComment momentComment) {
        Moment moment = baseRepository.getObjById(Moment.class, momentComment.getMomentId());
        if (moment == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "评论内容不存在!");
        }

        String userId = momentComment.getUserId();//评论用户id
        String replyUserId = momentComment.getReplyUserId();//回复用户id
        String momentUserId = moment.getUserId();//朋友圈用户id
        if (userId.equals(replyUserId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能自言自语哦！");
        }

        if (!momentUserId.equals(userId)) {//查看评论人是否对方好友
            ChatFriend chatFriend = chatFriendService.queryFriendDetailForUser(momentUserId, userId);
            if (chatFriend == null) {
                throw new BusinessException(CommonErrorResult.WECHAT_ERROR, "你不是该用户好友！");
            }
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在！");
        }
        UserInfoBase replyUser = null;
        if (StringUtils.isNotBlank(replyUserId)) {//有回复用户
            replyUser = baseRepository.getObjById(UserInfoBase.class, replyUserId);
            if (replyUser == null) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "回复用户不存在！");
            }
            //推送消息
            PushMsgVO pmv = new PushMsgVO(replyUserId, userId, user.getNickName() + " 回复了你的评论！", momentComment.getContent(),
                    moment.getId(), MessageLogType.MOMENT_COMMENT);
            ac.publishEvent(new PushEvent(pmv));//发送推送事件
        }
        MomentComment mc = new MomentComment();
        mc.setMomentId(momentComment.getMomentId());
        mc.setCreated(new Date());
        mc.setContent(momentComment.getContent());
        mc.setHeadImg(user.getUserPhotoHead());
        mc.setNickname(user.getNickName());
        mc.setState(MomentComment.NORMAL);
        mc.setUserId(userId);
        mc.setReplyUserId(replyUserId);
        mc.setReplyUserHeadImg(replyUser != null ? replyUser.getUserPhotoHead() : null);
        mc.setReplyUserNickname(replyUser != null ? replyUser.getNickName() : null);
        baseRepository.saveObj(mc);
        //朋友圈增加一次评论
        moment.setCommentCount(moment.getCommentCount() + 1);
        moment.setCommentTime(mc.getCreated());
        baseRepository.updateObj(moment);
        //评论人是发表人则不推送
        if (!momentUserId.equals(userId)) {
            PushMsgVO pmv = new PushMsgVO(momentUserId, userId, user.getNickName() + " 评论了你的朋友圈！", momentComment.getContent(),
                    moment.getId(), MessageLogType.MOMENT_COMMENT);
            ac.publishEvent(new PushEvent(pmv));//推送事件
        }
        return mc;
    }

    @Override
    public void deleteMomentComment(String userId, Long momentCommentId) {
        MomentComment mc = baseRepository.getObjById(MomentComment.class, momentCommentId);
        if (mc == null || !StringUtils.equals(userId, mc.getUserId())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "评论不存在！");
        }
        mc.setState(MomentComment.DELETE);
        baseRepository.updateObj(mc);
        //朋友圈评论-1
        String hql = "update Moment set commentCount = commentCount - 1 where id = " + mc.getMomentId();
        baseRepository.executeHql(hql, null);
    }

    @Override
    public Map<Long, List<MomentComment>> queryMomentCommentsMap(Collection<Long> momentIds) {
        if (momentIds == null || momentIds.isEmpty()) {
            return new HashMap<>();
        }
        String hql = "from MomentComment where state = :state and momentId in :ids order by momentId,created ";
        Map<String, Object> map = baseRepository.paramMap();
        map.put("ids", momentIds);
        map.put("state", MomentComment.NORMAL);
        List<MomentComment> comments = baseRepository.queryHqlResult(hql, map, 0, CutPage.MAX_COUNT);
        //设置用户信息
        Set<String> userSet = new HashSet<>();
        comments.forEach(c -> {
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
                c.setHeadImg(user.getHeadImg());
                c.setNickname(user.getNickname());
            }
            SimpleUserVO replyUser = userMap.get(c.getReplyUserId());
            if (replyUser != null) {
                c.setReplyUserHeadImg(replyUser.getHeadImg());
                c.setReplyUserNickname(replyUser.getNickname());
            }
        });
        return comments.stream().collect(Collectors.groupingBy(MomentComment::getMomentId));
    }

    @Override
    @Transactional
    public void addMomentVote(Long momentId, String userId) {
        Moment moment = baseRepository.getObjById(Moment.class, momentId);
        if (moment == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不存在的朋友圈！");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "用户不存在！");
        }
        String momentUserId = moment.getUserId();
        if (!momentUserId.equals(userId)) {//查看点赞人是否对方好友
            ChatFriend chatFriend = chatFriendService.queryFriendDetailForUser(momentUserId, userId);
            if (chatFriend == null) {
                throw new BusinessException(CommonErrorResult.WECHAT_ERROR, "你不是该用户好友！");
            }
        }
        MomentVoteUser momentVoteUser = checkMomentVoteByUser(momentId, userId);
        if (momentVoteUser != null) {
            if (momentVoteUser.getState() == CollectState.COLLECTION.getValue()) {//有记录且已点赞
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "重复点赞！");
            } else {//有记录且已取消点赞
                momentVoteUser.setState(CollectState.COLLECTION.getValue());
                momentVoteUser.setOperationTime(new Date());
                baseRepository.updateObj(momentVoteUser);
                //添加点赞数量
                moment.setVoteCount(moment.getVoteCount() + 1);
                moment.setVoteTime(new Date());
                baseRepository.updateObj(moment);
                return;
            }
        }
        MomentVoteUser mvu = new MomentVoteUser();
        mvu.setMomentId(momentId);
        mvu.setUserId(userId);
        mvu.setState(CollectState.COLLECTION.getValue());
        mvu.setCreated(new Date());
        mvu.setOperationTime(new Date());
        mvu.setHeadImg(user.getUserPhotoHead());
        mvu.setNickname(user.getNickName());
        baseRepository.saveObj(mvu);
        //添加点赞数量
        moment.setVoteCount(moment.getVoteCount() + 1);
        moment.setVoteTime(new Date());
        baseRepository.updateObj(moment);
    }

    @Override
    @Transactional
    public void cancelMomentVote(Long momentId, String userId) {
        if (momentId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "momentId不能为空！");
        }
        if (StringUtils.isBlank(userId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "userId不能为空！");
        }
        MomentVoteUser momentVoteUser = checkMomentVoteByUser(momentId, userId);
        if (momentVoteUser != null && momentVoteUser.getState() != CollectState.CANCEL_COLLECT.getValue()) {
            momentVoteUser.setState(CollectState.CANCEL_COLLECT.getValue());
            momentVoteUser.setOperationTime(new Date());//取消收藏时间
            baseRepository.updateObj(momentVoteUser);
            //减少点赞数量
            String hql = " update Moment set voteCount = voteCount - 1 where id = :momentId";
            Map<String, Object> params = new HashMap<>();
            params.put("momentId", momentId);
            baseRepository.executeHql(hql, params);
        }
    }

    @Override
    public MomentVoteUser checkMomentVoteByUser(Long momentId, String userId) {
        if (momentId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "momentId不能为空！");
        }
        if (StringUtils.isBlank(userId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "userId不能为空！");
        }
        String hql = "from MomentVoteUser where momentId = :momentId and userId = :userId";
        Map<String, Object> map = new HashMap<>();
        map.put("momentId", momentId);
        map.put("userId", userId);
        return baseRepository.executeHql(hql, map);
    }

    @Override
    public Map<Long, List<MomentVoteUser>> queryMomentVotesMap(Collection<Long> momentIds) {
        if (momentIds == null || momentIds.isEmpty()) {
            return new HashMap<>();
        }
        String hql = "from MomentVoteUser where state = :state and momentId in :ids order by operationTime desc";
        Map<String, Object> map = baseRepository.paramMap();
        map.put("state", MomentVoteUser.NORMAL);
        map.put("ids", momentIds);
        List<MomentVoteUser> collect = baseRepository.queryHqlResult(hql, map, 0, CutPage.MAX_COUNT);
        Set<String> userIds = collect.stream().map(MomentVoteUser::getUserId).collect(Collectors.toSet());
        Map<String, SimpleUserVO> userMap = userInfoBaseService.querySimpleUserMap(userIds);
        collect.forEach(mv -> {
            SimpleUserVO user = userMap.get(mv.getUserId());
            if (user != null) {
                mv.setNickname(user.getNickname());
                mv.setHeadImg(user.getHeadImg());
            }
        });
        return collect.stream().collect(Collectors.groupingBy(MomentVoteUser::getMomentId));
    }

    @Override
    public void realDeleteMoment(Long momentId) {
        String momentHql = "delete from Moment where id = :momentId";
        String fileHql = "delete from MomentFile where momentId = :momentId";
        String commentHql = "delete from MomentComment where momentId = :momentId";
        String voteUserHql = "delete from MomentVoteUser where momentId = :momentId";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("momentId", momentId);
        baseRepository.executeHql(momentHql, paramMap);
        baseRepository.executeHql(fileHql, paramMap);
        baseRepository.executeHql(commentHql, paramMap);
        baseRepository.executeHql(voteUserHql, paramMap);

    }

    @Override
    public CutPage<Moment> queryAllMoment(Integer state, String userId, String keyword, Integer page, Integer limit) {
        StringBuilder sql = new StringBuilder(100);
        Map<String, Object> map = baseRepository.paramMap();
        sql.append("SELECT * FROM moment m LEFT JOIN user_info_base u ON (m.user_id = u.id) WHERE m.state = :state ");
        map.put("state", state);
        if (StringUtils.isNoneBlank(userId)) {
            sql.append(" AND m.user_id = :userId ");
            map.put("userId",userId);
        }
        if (StringUtils.isNoneEmpty(keyword)) {
            sql.append(" AND (m.content LIKE :keyword OR u.nick_name LIKE :keyword OR u.id LIKE :keyword) ");
            map.put("keyword", "%" + keyword + "%");
        }
        sql.append(" ORDER BY m.created DESC");
        CutPage<Moment> cutPage = baseRepository.executeSql(sql, map, Moment.class, page, limit);
        this.setMomentsUser(cutPage.getiData());
        return cutPage;
    }

    @Override
    @Transactional
    public void updateMoment(Long momentId, String content, Date created) {
        Moment moment = baseRepository.getObjById(Moment.class, momentId);
        if (moment == null) {
            throw new  BusinessException(CommonErrorResult.BUSINESS_ERROR,"朋友圈不存在");
        }
        moment.setContent(content);
        moment.setCreated(created);
        baseRepository.updateObj(moment);
    }

    private void setMomentsUser(Collection<Moment> moments) {
        Set<String> userIds = moments.stream().map(Moment::getUserId).collect(Collectors.toSet());
        Map<String, SimpleUserVO> userMap = userInfoBaseService.querySimpleUserMap(userIds);
        moments.forEach(m -> {
            SimpleUserVO user = userMap.get(m.getUserId());
            if (user != null) {
                m.setHeadImg(user.getHeadImg());
                m.setNickname(user.getNickname());
            }
        });
    }
}
