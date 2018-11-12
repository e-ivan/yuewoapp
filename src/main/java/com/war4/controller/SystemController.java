package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.enums.AdPageLocation;
import com.war4.enums.AdPageTypeVar;
import com.war4.enums.HomePageType;
import com.war4.pojo.*;
import com.war4.vo.AdPageParamVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2016/12/7.
 */

@Controller
@RequestMapping(value = "system")
public class SystemController extends BaseController{

    //添加广告
    @RequestMapping(value = "addHomePage",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addHomePage(HomePage page){
        HomePage homePage = homePageService.addHomePage(page);
        return new ObjectResponse<>(homePage);
    }

    //添加宣传页面
    @RequestMapping(value = "addAdPage",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addAdPage(AdPageParamVO vo){
        adPageService.createAdPage(vo);
        return new Response(vo.getAdId() == null ? "添加成功" : "修改成功");
    }

    //取消宣传页
    @RequestMapping(value = "updateAdPageState",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateAdPageState(Long adId,boolean state){
        adPageService.updateAdPageState(adId,state);
        return new Response("已取消");
    }

    //查询宣传页面
    @RequestMapping(value = "queryAdPage",method = {RequestMethod.POST,RequestMethod.GET})
    public
    @ResponseBody
    Response queryAdPage(Integer type){
        return new ObjectResponse<>(adPageService.queryAdPage(type));
    }

    //查询所有宣传页面
    @RequestMapping(value = "queryAllAdPage",method = {RequestMethod.POST,RequestMethod.GET})
    public
    @ResponseBody
    Response queryAllAdPage(Integer state,Integer type,Integer page,Integer limit){
        return new ObjectResponse<>(adPageService.queryAllAdPage(state, type, page, limit));
    }

    //获取指定宣传页面
    @RequestMapping(value = "getAdPage",method = {RequestMethod.POST,RequestMethod.GET})
    public
    @ResponseBody
    Response getAdPage(Long adId){
        return new ObjectResponse<>(baseRepository.getObjById(AdPage.class,adId));
    }

    //删除
    @RequestMapping(value = "deleteHomePage",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteHomePage(String id){
         homePageService.deleteHomePage(id);
        return new Response("删除成功！");
    }
    //列表
    @RequestMapping(value = "queryHomePageList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryHomePageList(Integer page, Integer limit){
        List<AdPage> adPages = adPageService.queryAdPage(AdPageTypeVar.AD_HOME_BANNER.getCode());
        List<HomePage> homePages = adPages.stream().map(a -> {
            HomePage homePage = new HomePage();
            AdPageLocation location = AdPageLocation.getByCode(a.getLocation());
            if (location != null) {
                switch (location) {
                    case APP_MOVIE:
                        homePage.setMovieId(a.getValue());
                        homePage.setUrl("");
                        break;
                    case BROWSER:
                        homePage.setUrl(a.getValue());
                        break;
                }
            }
            homePage.setContent(a.getContent());
            homePage.setLocation(a.getLocation());
            homePage.setTitle(a.getTitle());
            homePage.setPhotoUrl(a.getImage().getBanner());
            homePage.setPopUpPicUrl(a.getImage().getPop());
            homePage.setSharePicUrl(a.getImage().getShare());
            homePage.setType(HomePageType.CAROUSEL.getCode());
            return homePage;
        }).collect(Collectors.toList());
//        CutPage cutPage = homePageService.queryHomePageList(UserContext.getUserId(),page, limit);
        CutPage<HomePage> cutPage = new CutPage<>(page, limit);
        cutPage.setiData(homePages);
        return new ObjectResponse<>(cutPage);
    }

   //添加活动
    @RequestMapping(value = "addActivity",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addActivity(Activity act){
        Activity activity = activityService.addActivity(act);
        return new ObjectResponse<>(activity);
    }
    //删除活动
    @RequestMapping(value = "deleteActivity",method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteActivity(String activityId){
        activityService.deleteActivityById(activityId);
        return new Response("删除成功！");
    }
    //活动列表
    @RequestMapping(value = "queryActivityList",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryActivityList(Integer type, Integer page, Integer limit){
        CutPage cutPage = activityService.queryActivityList(type, page, limit);
        return new ObjectResponse<>(cutPage);
    }
    //系统参数查看
    @RequestMapping(value = "querySystemParameter",method = RequestMethod.POST)
    public
    @ResponseBody
    Response querySystemParameter(){
        SystemParameter systemParameter = systemParameterService.querySystemParameter();
        return new ObjectResponse<>(systemParameter);
    }
    //系统参数查看
    @RequestMapping(value = "updateSystemParameter",method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateSystemParameter(String servicePhone,String aboutUs,String help){
         systemParameterService.updateSystemParameter(servicePhone, aboutUs, help);
        return new Response("修改成功！");
    }

    //系统推送添加
    @RequestMapping(value = "addSyaMessageSys",method = RequestMethod.POST)
    public
    @ResponseBody
    Response addSyaMessageSys(@ModelAttribute MessageSys messageSys){
        messageSysService.addSyaMessageSys(messageSys);
        return new Response("添加成功！");
    }

    //系统推送列表
    @RequestMapping(value = "queryUserMessageSys",method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryUserMessageSys(String userId, Integer page, Integer limit){
        CutPage cutPage =  messageSysService.queryUserMessageSys(userId, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //添加homepage的分享圖片url
//    @RequestMapping(value = "addHomePageSharePicUrl",method = RequestMethod.POST)
//    public
//    @ResponseBody
//    Response addHomePageSharePicUrl(String pid){
//         homePageService.saveHomePageSharePicUrl(pid);
//        return new ObjectResponse<>("添加分享图片成功");
//    }


//    public static void main(String[] args) {
//
//        Runtime runtime=Runtime.getRuntime();
//
//        try{
//            Process ps = Runtime.getRuntime().exec("ffmpeg.exe -re -i dashengguilai.flv -c copy -f flv rtmp://pdlb958ef09.live.126.net/live/25b7621280634a3498fd0d90aac41ea7?wsSecret=91ec95f00ee5f909a3f4c48027843507&wsTime=1482995962" ,null,new File("C:\\Users\\Administrator\\Desktop\\千年影院"));
////            runtime.exec("cmd.exe /k cd  C:\\Users\\Administrator\\Desktop\\千年影院   " +
////                    "ffmpeg.exe -re -i 西游记之大圣归来_超清.flv -c copy -f flv rtmp://pdlb958ef09.live.126.net/live/25b7621280634a3498fd0d90aac41ea7?wsSecret=91ec95f00ee5f909a3f4c48027843507&wsTime=1482995962 ");
////            Process exec = runtime.exec(" cmd /k cd  C:\\Users\\Administrator\\Desktop\\千年影院 " +
////                    "cmd /k  ffmpeg -re -i 西游记之大圣归来_超清.flv -c copy -f flv rtmp://pdlb958ef09.live.126.net/live/25b7621280634a3498fd0d90aac41ea7?wsSecret=91ec95f00ee5f909a3f4c48027843507&wsTime=1482995962");
//
//        }catch(Exception e){
//            System.out.println(e.toString());
//            System.out.println("Error!");
//
//        }
//
//    }
}
