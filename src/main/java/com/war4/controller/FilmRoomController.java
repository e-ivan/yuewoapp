package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.pojo.FilmRoom;
import com.war4.service.FilmRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2016/12/30.
 */
@Controller
@RequestMapping(value = "/filmroom")
public class FilmRoomController extends BaseController{

    @RequestMapping(value="/addFilmRoom",method= RequestMethod.POST)
    public @ResponseBody
    Response addFilmRoom(@ModelAttribute FilmRoom insertObj) throws Exception
    {
        FilmRoom filmRoom =  filmRoomService.addFilmRoom( insertObj);
        return new ObjectResponse<>(filmRoom);
    }

    @RequestMapping(value="/joinFilmRoom",method= RequestMethod.POST)
    public @ResponseBody
    Response joinFilmRoom(String fkUserId, String fkRoomId) throws Exception
    {
        filmRoomService.joinFilmRoom( fkUserId, fkRoomId);
        return new Response("加入房间成功！");
    }


    @RequestMapping(value="/addAnchorUserGift",method= RequestMethod.POST)
    public @ResponseBody
    Response addAnchorUserGift(String roomId, String fkUserId, String giftId, Integer count) throws Exception
    {
        filmRoomService.addAnchorUserGift( roomId, fkUserId, giftId, count);
        return new Response("赠送礼物成功！");
    }

    @RequestMapping(value="/queryFilmRoom",method= RequestMethod.POST)
    public @ResponseBody
    Response queryFilmRoom(String roomId) throws Exception
    {
       FilmRoom filmRoom =  filmRoomService.queryFilmRoom( roomId);
        return new ObjectResponse<>(filmRoom);
    }

    @RequestMapping(value="/queryFileRoomList",method= RequestMethod.POST)
    public @ResponseBody
    Response queryFileRoomList(String name, Integer page, Integer limit) throws Exception
    {
        CutPage cutPage =  filmRoomService.queryFileRoomList( name, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    @RequestMapping(value="/queryFilmRoomUsers",method= RequestMethod.POST)
    public @ResponseBody
    Response queryFilmRoomUsers(String roomId) throws Exception
    {
        CutPage cutPage =  filmRoomService.queryFilmRoomUsers(roomId);
        return new ObjectResponse<>(cutPage);
    }


    @RequestMapping(value="/deleteFilmRoomUser",method= RequestMethod.POST)
    public @ResponseBody
    Response deleteFilmRoomUser(String fkUserId,String fkRoomId) throws Exception
    {
        filmRoomService.deleteFilmRoomUser(fkUserId, fkRoomId);
        return new Response("离开成功！");
    }

    @RequestMapping(value="/closeFilmRoon",method= RequestMethod.POST)
    public @ResponseBody
    Response closeFilmRoon(String roomId) throws Exception
    {
        filmRoomService.closeFilmRoon(roomId);
        return new Response("关闭成功！");
    }

}
