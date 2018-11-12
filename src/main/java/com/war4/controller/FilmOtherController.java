package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.enums.CollectState;
import com.war4.pojo.*;
import com.war4.service.FilmCollectionService;
import com.war4.service.FilmCommentService;
import com.war4.service.FilmTopicService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/1/4.
 */
@Controller
@RequestMapping(value = "/filmother")
public class FilmOtherController extends BaseController{


    //查询所有话题
    @RequestMapping(value = "queryAllFilmTopics",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllFilmTopics(String orderBy,Integer page,Integer limit){
        return new ObjectResponse<>(filmTopicService.queryAllFilmTopic(orderBy, page, limit));
    }
    //电影话题发布
    @RequestMapping(value = "addFilmTopic",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addFilmTopic(@ModelAttribute FilmTopic filmTopic){
        filmTopicService.addFilmTopic(filmTopic);
        return new Response("发表成功！");
    }
    //电影话题详情
    @RequestMapping(value = "queryFilmTopicById",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmTopicById(String topicId){
        FilmTopic filmTopic =  filmTopicService.queryFilmTopicById(topicId);
        return new ObjectResponse<>(filmTopic);
    }

    //电影话题删除
    @RequestMapping(value = "deleteFilmTopic",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteFilmTopic(String topicId,String fkUserId){
        filmTopicService.deleteFilmTopic(topicId,fkUserId);
        return new Response("删除成功！");
    }

    //电影话题列表
    @RequestMapping(value = "queryFilmTopics",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmTopics(String filmId,Integer page,Integer limit){
        CutPage cutPage =  filmTopicService.queryFilmTopics(filmId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //我的电影话题列表
    @RequestMapping(value = "queryFilmTopicsForUser",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmTopicsForUser(String fkUserId,Integer page,Integer limit){
        CutPage cutPage =  filmTopicService.queryFilmTopicsForUser(fkUserId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //添加话题评论
    @RequestMapping(value = "addFilmTopicComment",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addFilmTopicComment(@ModelAttribute FilmTopicComment comment){
        filmTopicService.addFilmTopicComment(comment);
        return new Response("评论成功！");
    }

    /**
     * 删除电影话题评论
     */
    @RequestMapping(value = "deleteFilmTopicComment",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteFilmTopicComment(String commentId, String userId){
        filmTopicService.deleteFilmTopicComment(commentId, userId);
        return new Response("删除成功！");
    }

    //查询回复的评论
    @RequestMapping(value = "queryFilmTopicReturnCommtents",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmTopicReturnCommtents(String returnCommentId, Integer page, Integer limit){
        CutPage cutPage =  filmTopicService.queryFilmTopicReturnCommtents(returnCommentId, page, limit);
        return new ObjectResponse<>(cutPage);
    }
    //查询话题评论列表
    @RequestMapping(value = "queryFilmTopicCommtents",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmTopicCommtents(String topicId,Integer page,Integer limit){
        CutPage cutPage = filmTopicService.queryFilmTopicCommtents(topicId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //收藏电影
    @RequestMapping(value = "addFilmCollection",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addFilmCollection(String fkUserId,String fkFilmId) throws Exception {
        filmCollectionService.addFilmCollection(fkUserId, fkFilmId);
        return new Response("收藏成功！");
    }

    //取消收藏
    @RequestMapping(value = "deleteFilmCollection",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteFilmCollection(String fkUserId,String fkFilmId){
        filmCollectionService.cancelFilmCollection(fkUserId, fkFilmId);
        return new Response("取消收藏成功！");
    }

    //查看电影是否被收藏
    @RequestMapping(value = "checkFilmIsCollection",method = RequestMethod.POST)
    public
    @ResponseBody
    Response checkFilmIsCollection(String fkUserId,String fkFilmId){
        FilmCollection filmCollection = filmCollectionService.checkFilmIsCollection(fkUserId, fkFilmId);
        boolean flag = false;
        if(filmCollection!=null && filmCollection.getState() == CollectState.COLLECTION.getValue()){
            flag = true;
        }
        String returnStr = "{'isCollection':'"+flag+"'}";
        JSONObject jsonObject =JSONObject.fromObject(returnStr);
        return new ObjectResponse<>(jsonObject);
    }

    //我的收藏
    @RequestMapping(value = "queryMyCollection",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyCollection(String fkUserId,Integer page,Integer limit) throws Exception {
        CutPage cutPage =  filmCollectionService.queryMyCollection(fkUserId, page,limit);
        return new ObjectResponse<>(cutPage);
    }

    //发表影评
    @RequestMapping(value = "addFilmComment",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addFilmComment(@ModelAttribute FilmComment filmComment){
        filmCommentService.addFilmComment(filmComment);
        return new Response("发表影评成功！");
    }

    //删除影评
    @RequestMapping(value = "deleteFilmComment",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteFilmComment(String commentId){
        filmCommentService.deleteFilmComment(commentId);
        return new Response("删除影评成功！");
    }

    //影评列表
    @RequestMapping(value = "queryFilmComments",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmComments(String filmId,Integer page,Integer limit){
        CutPage cutPage =  filmCommentService.queryFilmComments(filmId, page, limit);
        return new ObjectResponse<>(cutPage);
    }
    //我的影评
    @RequestMapping(value = "queryMyFilmComment",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyFilmComment(String fkUserId,Integer page,Integer limit){
        CutPage cutPage =  filmCommentService.queryMyFilmComment(fkUserId, page, limit);
        return new ObjectResponse<>(cutPage);
    }
}
