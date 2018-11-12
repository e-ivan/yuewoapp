package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.AnchorFan;
import com.war4.pojo.AnchorRoom;
import com.war4.pojo.Trailer;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2016/12/12.
 */
@Controller
@RequestMapping(value = "/anchorroom")
public class AnchorRoomController extends BaseController{

    //添加直播
    @RequestMapping(value = "addAnchorroom",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addAnchorroom(String userId, String name) throws Exception {
        AnchorRoom anchorRoom = anchorRoomService.addAnchorRoom(userId, name);
        return new ObjectResponse<>(anchorRoom);
    }

    //关闭直播
    @RequestMapping(value = "closeAnchorroom",method = RequestMethod.POST)
    public
    @ResponseBody
    Response closeAnchorroom(String roomId) throws Exception {
        anchorRoomService.closeAnchorRoom(roomId);
        return new Response("已经关闭直播！");
    }

    //操作直播录制
    @RequestMapping(value = "operationRecording",method = RequestMethod.POST)
    public
    @ResponseBody
    Response operationRecording(String userId,String roomId, Integer needRecord, Integer format, Integer duration,String filename) throws Exception {
        anchorRoomService.operationRecording(userId,roomId, needRecord, format, duration,filename);
        return new Response("操作成功！");
    }
    @RequestMapping(value = "setVoide",method = RequestMethod.POST)
    public
    @ResponseBody
    Response setVoide(String cid,String userId) throws Exception {
        anchorRoomService.setVoide(cid, userId);
        return new Response("操作成功！");
    }
    @RequestMapping(value = "joinAnchorroom",method = RequestMethod.POST)
    public
    @ResponseBody
    Response joinAnchorroom(String roomId,String userId) throws Exception {
        anchorRoomService.joinAnchorRoom(roomId, userId);
        return new Response("已加入直播！");
    }

    //赠送礼物
    @RequestMapping(value = "addAnchorUserGift",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addAnchorUserGift(String roomId,String fkUserId,String giftId, Integer count) throws Exception {
        anchorRoomService.addAnchorUserGift(roomId, fkUserId, giftId, count);
        return new Response("赠送成功！");
    }
    //添加关注
    @RequestMapping(value = "addAnchorFan",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addAnchorFan(String userId, String anchorId) throws Exception {
        anchorFanService.addAnchorFan(userId, anchorId);
        return new Response("关注成功！");
    }

    //取消关注
    @RequestMapping(value = "deleteAnchorFan",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteAnchorFan(String userId, String anchorId,Integer roomId) throws Exception {
        anchorFanService.deleteAnchorFan(userId, anchorId);

        return new Response("取消关注成功！");
    }

    //查看我的粉丝
    @RequestMapping(value = "queryMyFan",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyFan(String anchorId,Integer page,Integer limit) throws Exception {
        CutPage cutPage = anchorFanService.queryMyFan(anchorId,page,limit);
        return new ObjectResponse<>(cutPage);
    }

    //查看我的关注的
    @RequestMapping(value = "queryMyAnchor",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyAnchor(String userId,Integer page,Integer limit) throws Exception {
        CutPage cutPage = anchorFanService.queryMyAnchor(userId,page,limit);
        return new ObjectResponse<>(cutPage);
    }

    //查看我的关注的主播的直播列表
    @RequestMapping(value = "queryMyAnchorRoom",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyAnchorRoom(String userId,Integer role ,Integer page,Integer limit) throws Exception {
        CutPage cutPage = anchorRoomService.queryMyAttentionAnchor(userId,role,page,limit);
        return new ObjectResponse<>(cutPage);
    }

    //查看我的关注的主播的直播列表
    @RequestMapping(value = "queryNearAnchor",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryNearAnchor(String x,String y,Integer role ,Integer sex,Integer page,Integer limit) throws Exception {
        CutPage cutPage = anchorRoomService.queryNearAnchor(x, y,role,sex, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    @RequestMapping(value = "queryRoomPeopleCount",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryRoomPeopleCount(String roomId) throws Exception {
        Integer count = anchorRoomService.queryRoomPeopleCount(roomId);
        return new ObjectResponse<>(count);
    }

    @RequestMapping(value = "queryHostAnchor",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryHostAnchor(Integer role,Integer page, Integer limit) throws Exception {
        CutPage cutPage = anchorRoomService.queryHostAnchor(role,page,limit);
        return new ObjectResponse<>(cutPage);
    }
    @RequestMapping(value = "checkIsMyAttention",method = RequestMethod.POST)
    public
    @ResponseBody
    Response checkIsMyAttention(String userId, String anchorId) throws Exception {
        AnchorFan anchorFan = anchorFanService.checkIsMyAttention(userId, anchorId);
        String flag = "false";

        if(anchorFan!=null){
            flag = "true";
        }
        String returnStr = "{'isAttention':'"+flag+"'}";
        JSONObject obj = JSONObject.fromObject(returnStr);
        return new ObjectResponse<>(obj);
    }

    //添加直播预告
    @RequestMapping(value = "addTrailer",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addTrailer(String userId,String startTime,String title,String content) throws Exception {
        Trailer count = anchorRoomService.addTrailer(userId, startTime, title, content);
        return new ObjectResponse<>(count);
    }

    @RequestMapping(value = "queryTrailerList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryTrailerList(String userId,Integer page,Integer limit) throws Exception {
        CutPage<Trailer>  count = anchorRoomService.queryTrailerList(userId, page, limit);
        return new ObjectResponse<>(count);
    }

    @RequestMapping(value = "queryTrailer",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryTrailer(String id) throws Exception {
        Trailer  count = anchorRoomService.queryTrailer(id);
        return new ObjectResponse<>(count);
    }

    @RequestMapping(value = "queryAnchorRooms",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAnchorRooms(Integer type,Integer page,Integer limit) throws Exception {
        CutPage<AnchorRoom>  count = anchorRoomService.queryAnchorRooms(type, page, limit);
        return new ObjectResponse<>(count);
    }

    @RequestMapping(value = "updateAnchorRoom",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateAnchorRoom(String id, Integer type) throws Exception {
        AnchorRoom  count = anchorRoomService.updateAnchorRoom(id, type);
        return new ObjectResponse<>(count);
    }

    @RequestMapping(value = "findAnchorRoom",method = RequestMethod.POST)
    public
    @ResponseBody
    Response findAnchorRoom(String roomId) throws Exception {
       String  count = anchorRoomService.findAnchorRoom(roomId);
        return new ObjectResponse<>(count);
    }
}
