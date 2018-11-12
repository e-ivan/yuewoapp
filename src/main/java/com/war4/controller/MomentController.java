package com.war4.controller;

import com.war4.base.*;
import com.war4.enums.CollectState;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.Moment;
import com.war4.pojo.MomentComment;
import com.war4.pojo.MomentVoteUser;
import com.war4.util.UserContext;
import net.sf.json.JSONObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 朋友圈控制器
 * Created by hh on 2017.7.15 0015.
 */
@Controller
@RequestMapping("moment")
public class MomentController extends BaseController {


    /**
     * 发布朋友圈
     *
     * @param files
     * @param moment
     */
    @RequestMapping(value = "publishMoment", method = RequestMethod.POST)
    @ResponseBody
    public Response publishMoment(@RequestParam("files") MultipartFile[] files, Moment moment) {
        momentService.saveMoment(files, moment);
        return new Response("发布成功");
    }

    /**
     * 获取好友的朋友圈
     */
    @RequestMapping(value = "queryFriendMoments", method = RequestMethod.POST)
    @ResponseBody
    public Response queryFriendMoments(boolean yourself, Integer page, Integer limit) throws Exception {
        CutPage<Moment> momentCutPage = momentService.queryFriendMoments(UserContext.getUserId(), yourself, page, limit);
        return new ObjectResponse<>(momentCutPage);
    }

    /**
     * 查询自己的朋友圈
     */
    @RequestMapping(value = "queryMyMoment", method = RequestMethod.POST)
    @ResponseBody
    public Response queryMyMoment(Integer page, Integer limit) throws Exception {
        CutPage<Moment> momentCutPage = momentService.queryMyMoment(UserContext.getUserId(), page, limit);
        return new ObjectResponse<>(momentCutPage);
    }

    /**
     * 根据id获取朋友圈信息
     *
     * @param momentId
     */
    @RequestMapping(value = "getMomentById", method = RequestMethod.POST)
    @ResponseBody
    public Response getMomentById(Long momentId) throws Exception {
        return new ObjectResponse<>(momentService.getMomentById(momentId, UserContext.getUserId()));
    }

    /**
     * 删除朋友圈
     *
     * @param userId
     * @param momentId
     */
    @RequestMapping(value = "deleteMoment", method = RequestMethod.POST)
    @ResponseBody
    public Response deleteMoment(String userId, Long momentId) throws Exception {
        momentService.updateMomentState(userId, momentId, false);
        return new Response("删除成功！");
    }

    /**
     * 添加朋友圈评论
     *
     * @param momentComment
     */
    @RequestMapping(value = "addComment", method = RequestMethod.POST)
    @ResponseBody
    public Response addComment(MomentComment momentComment) throws Exception {
        return new ObjectResponse<>(momentService.addMomentComment(momentComment));
    }

    /**
     * 删除朋友圈评论
     *
     * @param userId
     * @param commentId
     */
    @RequestMapping(value = "deleteComment", method = RequestMethod.POST)
    @ResponseBody
    public Response deleteComment(String userId, Long commentId) throws Exception {
        momentService.deleteMomentComment(userId, commentId);
        return new Response("删除成功");
    }

    /**
     * 朋友圈点赞
     *
     * @param momentId
     * @param userId
     * @param vote     0：取消 1：点赞
     */
    @RequestMapping(value = "momentVote", method = RequestMethod.POST)
    @ResponseBody
    public Response momentVote(Long momentId, String userId, boolean vote) throws Exception {
        String exMsg = "点赞成功！";
        if (vote) {
            momentService.addMomentVote(momentId, userId);
        } else {
            momentService.cancelMomentVote(momentId, userId);
            exMsg = "取消点赞！";
        }
        return new Response(exMsg);
    }


    /**
     * 检查是否点赞
     *
     * @param momentId
     * @param userId
     */
    @RequestMapping(value = "checkVote", method = RequestMethod.POST)
    @ResponseBody
    public Response checkVote(Long momentId, String userId) throws Exception {
        MomentVoteUser momentVoteUser = momentService.checkMomentVoteByUser(momentId, userId);
        StringBuilder retJson = new StringBuilder(20);
        retJson.append("{'isVote':'").append(momentVoteUser != null && momentVoteUser.getState() == CollectState.COLLECTION.getValue()).append("'}");
        return new ObjectResponse<>(JSONObject.fromObject(retJson.toString()));
    }

    /**
     * 获取用户朋友圈背景
     *
     * @param userId
     */
    @RequestMapping(value = "momentBackground", method = RequestMethod.POST)
    @ResponseBody
    public Response momentBackground(String userId) throws Exception {
        String momentBackground = fileService.getCommonPhotoUrl("image", "imageMomentBackground", userId);
        return new ObjectResponse<>(momentBackground);
    }

    /**
     * 获取所有朋友圈
     */
    @RequestMapping(value = "queryAllMoment", method = RequestMethod.POST)
    @ResponseBody
    public Response queryAllMoment(Integer state, String userId,String keyword, Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(momentService.queryAllMoment(state, userId,keyword, page, limit));
    }

    /**
     * 更新朋友圈状态
     */
    @RequestMapping(value = "updateMomentState", method = RequestMethod.POST)
    @ResponseBody
    public Response updateMomentState(Long momentId, String userId, boolean state) throws Exception {
        momentService.updateMomentState(userId, momentId, state);
        return new Response(state ? "恢复成功" : "删除成功");
    }

    /**
     * 更新朋友圈内容
     */
    @RequestMapping(value = "updateMoment", method = RequestMethod.POST)
    @ResponseBody
    public Response updateMoment(Long momentId, String content, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date created) throws Exception {
        momentService.updateMoment(momentId, content, created);
        return new Response("修改成功");
    }

    /**
     * 获取简单的朋友圈对象
     */
    @RequestMapping(value = "getSimpleMoment", method = RequestMethod.POST)
    @ResponseBody
    public Response getSimpleMoment(Long momentId) throws Exception {
        return new ObjectResponse<>(baseRepository.getObjById(Moment.class, momentId));
    }


    @RequestMapping(value = "realDeleteMoment", method = RequestMethod.POST)
    @ResponseBody
    public Response realDeleteMoment(Long momentId, String password) throws Exception {
        String verifyPassword = "admin";
        if (verifyPassword.equals(password)) {
            momentService.realDeleteMoment(momentId);
        } else {
            throw new BusinessException(CommonErrorResult.WECHAT_ERROR, "你无权访问该接口!");
        }
        return new Response("厉害了我的哥！");
    }
}
