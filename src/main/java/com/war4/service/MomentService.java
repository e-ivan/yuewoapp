package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.Moment;
import com.war4.pojo.MomentComment;
import com.war4.pojo.MomentVoteUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 朋友圈服务
 * Created by hh on 2017.7.15 0015.
 */
public interface MomentService {
    /**
     * 添加朋友圈内容
     * @param files     图片数组
     * @param moment    朋友圈内容
     */
    void saveMoment(MultipartFile[] files, Moment moment);

    /**
     * 获取朋友发表的朋友圈
     * @param userId
     * @param yourself 是否显示自己
     */
    CutPage<Moment> queryFriendMoments(String userId,boolean yourself,Integer page,Integer limit);

    /**
     * 获取自己的朋友圈列表
     * @param userId    用户id
     */
    CutPage<Moment> queryMyMoment(String userId, Integer page, Integer limit);

    /**
     * 根据id获取朋友圈,如果传入userId，则在moment中；列出是否有点赞
     * @param momentId
     * @param userId
     */
    Moment getMomentById(Long momentId,String userId);

    /**
     * 更新朋友圈状态
     * @param userId
     * @param momentId
     * @param state
     */
    void updateMomentState(String userId, Long momentId, boolean state);

    /**
     * 添加朋友圈评论
     * @param momentComment
     */
    MomentComment addMomentComment(MomentComment momentComment);

    /**
     * 删除朋友圈评论
     * @param userId
     * @param momentCommentId
     */
    void deleteMomentComment(String userId,Long momentCommentId);

    /**
     * 获取朋友圈评论map
     * @param momentIds 朋友圈id
     * @return
     */
    Map<Long, List<MomentComment>> queryMomentCommentsMap(Collection<Long> momentIds);

    /**
     * 朋友圈点赞
     * @param momentId
     * @param userId
     */
    void addMomentVote(Long momentId,String userId);

    /**
     * 朋友圈取消赞
     * @param momentId
     * @param userId
     */
    void cancelMomentVote(Long momentId,String userId);

    /**
     * 查询点赞记录
     * @param momentId
     * @param userId
     */
    MomentVoteUser checkMomentVoteByUser(Long momentId,String userId);

    /**
     * 获取朋友圈点赞map
     * @param momentIds
     * @return
     */
    Map<Long, List<MomentVoteUser>> queryMomentVotesMap(Collection<Long> momentIds);

    /**
     * 真正删除朋友圈内容（不可逆）
     * @param momentId
     */
    void realDeleteMoment(Long momentId);

    /**
     * 获取所有朋友圈
     */
    CutPage<Moment> queryAllMoment(Integer state, String userId, String keyword, Integer page, Integer limit);

    /**
     * 更新朋友圈内容
     */
    void updateMoment(Long momentId, String content, Date created);
}
