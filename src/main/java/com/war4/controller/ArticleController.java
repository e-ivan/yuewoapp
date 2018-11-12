package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.enums.CollectState;
import com.war4.pojo.ArticleVoteUser;
import com.war4.pojo.Comment;
import com.war4.pojo.Content;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 文章控制器
 * Created by hh on 2017.7.8 0008.
 */
@Controller
@RequestMapping(value = "article")
public class ArticleController extends BaseController {

    /**
     * 查询所有或指定类型文章
     * @param module    类型
     */
    @RequestMapping(value = "queryArticleByModule",method = RequestMethod.POST)
    @ResponseBody
    public Response queryArticleByModule(String module,Integer page,Integer limit) throws Exception{
        CutPage<Content> contentCutPage = articleService.queryArticlesByModule(module, page, limit);
        return new ObjectResponse<>(contentCutPage);
    }

    /**
     * 根据文章id获取文章详情
     * @param id
     * @param userId
     */
    @RequestMapping(value = "selectArticleById",method = RequestMethod.POST)
    @ResponseBody
    public Response selectArticleById(Long id,String userId) throws Exception{
        return new ObjectResponse<>(articleService.selectArticleById(id,userId));
    }

    /**
     * 添加评论
     * @param comment
     */
    @RequestMapping(value = "addComment",method = RequestMethod.POST)
    @ResponseBody
    public Response addComment(Comment comment) throws Exception{
        articleService.addComment(comment);
        return new Response("评论成功！");
    }

    /**
     * 获取文章评论
     * @param contentId
     * @param orderBy
     */
    @RequestMapping(value = "queryArticleComments",method = RequestMethod.POST)
    @ResponseBody
    public Response queryArticleComments(Long contentId,String orderBy,Integer page,Integer limit) throws Exception{
        CutPage<Comment> commentCutPage = articleService.queryArticleComments(contentId,orderBy, page, limit);
        return new ObjectResponse<>(commentCutPage);
    }

    /**
     * 获取评论中的回复
     * @param commentId
     * @param orderBy
     */
    @RequestMapping(value = "queryCommentsInComment",method = RequestMethod.POST)
    @ResponseBody
    public Response queryCommentsInComment(Long commentId,String orderBy,Integer page,Integer limit) throws Exception{
        CutPage<Comment> commentCutPage = articleService.queryCommentsInComment(commentId, orderBy, page, limit);
        return new ObjectResponse<>(commentCutPage);
    }

    /**
     * 获取用户的浏览历史
     * @param userId
     * @param module
     */
    @RequestMapping(value = "queryUserViewHistory",method = RequestMethod.POST)
    @ResponseBody
    public Response queryUserViewHistory(String userId,String module,Integer page,Integer limit) throws Exception{
        return new ObjectResponse<>(userViewHistoryService.queryUserViewHistory(userId,module, page, limit));
    }

    /**
     * 文章点赞
     * @param userId
     * @param contentId
     */
    @RequestMapping(value = "addArticleVote",method = RequestMethod.POST)
    @ResponseBody
    public Response addArticleVote(Long contentId,String userId) throws Exception{
        articleService.addArticleVote(contentId,userId);
        return new Response("点赞成功！");
    }
    /**
     * 检查是否点赞
     * @param contentId
     * @param userId
     */
    @RequestMapping(value = "checkVote",method = RequestMethod.POST)
    @ResponseBody
    public Response checkVote(Long contentId,String userId) throws Exception{
        ArticleVoteUser articleVoteUser = articleService.checkArticleVoteByUser(contentId, userId);
        StringBuilder retJson = new StringBuilder(20);
        retJson.append("{'isVote':'").append(articleVoteUser!=null && articleVoteUser.getState() == CollectState.COLLECTION.getValue()).append("'}");
        return new ObjectResponse<>(JSONObject.fromObject(retJson.toString()));
    }

    /**
     * 取消文章点赞
     * @param contentId
     * @param userId
     */
    @RequestMapping(value = "cancelArticleVote",method = RequestMethod.POST)
    @ResponseBody
    public Response cancelArticleVote(Long contentId,String userId) throws Exception{
        articleService.cancelArticleVote(contentId,userId);
        return new Response("取消点赞！");
    }


    @RequestMapping(value = "collectArticle",method = RequestMethod.POST)
    @ResponseBody
    public Response collectArticle(String userId,String contentId) throws Exception{
        return new ObjectResponse<>(collectMappingService.collectArticle(userId, contentId));
    }
}
