package com.war4.controller;

import com.war4.base.*;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.DatingFilm;
import com.war4.pojo.DatingSetting;
import com.war4.util.NumberUtil;
import com.war4.util.UserContext;
import com.war4.vo.DatingUserLatestVO;
import com.war4.vo.NearbyDatingParamVO;
import com.war4.vo.QueryDatingUserVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 */
@Controller
@RequestMapping(value = "/dating")
public class DatingController extends BaseController {

    //添加约会设置
    @RequestMapping(value = "addDatingSetting", method = RequestMethod.POST)
    public
    @ResponseBody
    Response addDatingSetting(String fkUserId, Integer isAccept, String fkGiftId) throws Exception {
        datingSettingService.addDatingSetting(fkUserId, isAccept, fkGiftId);
        return new Response("添加成功！");
    }

    //查看约会设置
    @RequestMapping(value = "queryDatingSettingByUserId", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryDatingSettingByUserId(String fkUserId) throws Exception {
        DatingSetting datingSetting = datingSettingService.queryDatingSettingByUserId(fkUserId);
        return new ObjectResponse<>(datingSetting);
    }

    //添加约会设置
    @RequestMapping(value = "updateDatingSetting", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateDatingSetting(String fkUserId, Integer isAccept, String fkGiftId) throws Exception {
        datingSettingService.updateDatingSetting(fkUserId, isAccept, fkGiftId);
        return new Response("修改成功！");
    }

    //开始约人
    @RequestMapping(value = "addDatingFilm", method = RequestMethod.POST)
    public
    @ResponseBody
    Response addDatingFilm(DatingFilm datingFilm) throws Exception {
        datingFilmService.addDatingFilm(datingFilm);
        return new Response("约会成功！");
    }

    //约会详情
    @RequestMapping(value = "queryDatingFilmForId", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryDatingFilmForId(String datingId) throws Exception {
        DatingFilm datingFilm = datingFilmService.queryDatingFilmForId(datingId);
        return new ObjectResponse<>(datingFilm);
    }

    //我的约会
    @RequestMapping(value = "queryMyDating", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyDating(String userId, Integer type, Integer page, Integer limit) throws Exception {
        CutPage cutPage = datingFilmService.queryMyDating(userId, type, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //接受约会
    @RequestMapping(value = "updateDatingSettingAccept", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateDatingSettingAccept(String datingId) throws Exception {
        DatingFilm datingFilm = datingFilmService.updateDatingSettingAccept(datingId);
        return new ObjectResponse<>(datingFilm);
    }

    //取消约会或者拒绝约会
    @RequestMapping(value = "updateDatingSettingRefuse", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateDatingSettingRefuse(String datingId) throws Exception {
        datingFilmService.updateDatingSettingRefuse(datingId);
        return new ObjectResponse<>("取消约会成功");
    }

    //取消约会或者拒绝约会
    @RequestMapping(value = "deleteDatingSetting", method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteDatingSetting(String datingId) throws Exception {
        datingFilmService.deleteDatingSetting(datingId);
        return new ObjectResponse<>("删除约会成功");
    }

    /**
     * @param vo lng   经度
     * @param vo lat   纬度
     */
    @RequestMapping(value = "queryDatingUser", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryDatingUser(QueryDatingUserVO vo) throws Exception {
//        CutPage cutPage =  datingFilmService.queryDatingUser(vo);
        CutPage<DatingUserLatestVO> cutPage = new CutPage<>(vo.getPage(), vo.getLimit());
        Long totalCount = datingFilmService.queryDatingUserCount(vo);
        int limit = cutPage.getLimit();
        //获取真实会员
        vo.setInternal(0);
        vo.setLimit(limit / 2 == 0 ? 1 : limit / 2);
        CutPage<DatingUserLatestVO> realUser = datingFilmService.queryDatingUserLatest(vo);
        Long realTotal = realUser.getTotalCount();
//        boolean flag = totalCount / 2 <= realTotal;
//        if (flag) {
        cutPage.setTotalCount(realTotal * 2);
//        } else {
//            cutPage.setTotalCount((totalCount - realTotal) * 2);
//        }
        //获取内部会员
        List<DatingUserLatestVO> realList = realUser.getiData();
        datingFilmService.queryUserImageAlbumCount(realList);
        if (vo.getPage() <= cutPage.getTotalPage() - 1) {
            int internalLimit = limit - realList.size();
            vo.setInternal(1);
            int inTotal = totalCount.intValue() - realTotal.intValue();
            int inTotalPage = inTotal % limit == 0 ? inTotal / internalLimit : inTotal / internalLimit + 1;
            int randomPage = NumberUtil.getAroundVal((double) inTotalPage, (double) 0, 1).intValue();
            vo.setPage(inTotalPage > 1 && inTotalPage - 1 == randomPage ? randomPage - 1 : randomPage);
            vo.setLimit(internalLimit);
            CutPage<DatingUserLatestVO> internalUser = datingFilmService.queryDatingUserLatest(vo);
            List<DatingUserLatestVO> internalList = internalUser.getiData();
            datingFilmService.queryUserImageAlbumCount(internalList);
            int min = realList.get(0).getDistance().intValue();
            int max = realList.get(realList.size() - 1).getDistance().intValue();
            List<Integer> randomDist = NumberUtil.randomArray(min, max == min ? max + 1000 : max, internalList.size());
            for (int j = 0; j < internalList.size(); j++) {
                internalList.get(j).setDistance((double) randomDist.get(j));
            }
            realList.addAll(internalList);
        }
        //随机排序
        Collections.shuffle(realList);
        cutPage.setiData(realList);
        return new ObjectResponse<>(cutPage);
    }

    @RequestMapping(value = "evaluateDating", method = RequestMethod.POST)
    public
    @ResponseBody
    Response evaluateDating(String userId, String appraiser, Float fenShu, String content) throws Exception {
        datingFilmService.evaluateDating(userId, appraiser, fenShu, content);
        return new Response("评价成功");
    }

    @RequestMapping(value = "queryUserEvaluate", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryUserEvaluate(String userId, Integer page, Integer limit) throws Exception {
        CutPage cutPage = datingFilmService.queryUserEvaluate(userId, page, limit);
        return new ObjectResponse<>(cutPage);
    }


    @RequestMapping(value = "queryAllDatingFilm", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllDatingFilm(String status, String keyword, Integer page, Integer limit) throws Exception {
        CutPage<DatingFilm> datingFilmCutPage = datingFilmService.queryAllDating(status, keyword, page, limit);
        return new ObjectResponse<>(datingFilmCutPage);
    }

    /**
     * 创建附近有约
     */
    @RequestMapping(value = "createNearbyDating", method = RequestMethod.POST)
    public
    @ResponseBody
    Response createNearbyDating(NearbyDatingParamVO vo) throws Exception {
        nearbyDatingService.createNearbyDating(vo);
        return new Response("发布成功！");
    }

    /**
     * 删除附近有约
     */
    @RequestMapping(value = "delNearbyDating", method = {RequestMethod.POST,RequestMethod.GET})
    public
    @ResponseBody
    Response delNearbyDating(Long nId,String createUser) throws Exception {
        if (createUser == null) {
            createUser = UserContext.getUserId();
        }
        nearbyDatingService.cancelNearbyDating(nId,createUser);
        return new Response("已删除！");
    }

    /**
     * 附近有约报名
     */
    @RequestMapping(value = "applyNearbyDating", method = {RequestMethod.POST,RequestMethod.GET})
    public
    @ResponseBody
    Response applyNearbyDating(Long nId) throws Exception {
        try {
            nearbyDatingService.createNearbyDatingApplyItem(nId);
        }catch (Exception e){
            if (!(e instanceof BusinessException)) throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"你已报名");
            throw e;
        }
        return new Response("报名成功！");
    }

    /**
     * 根据id获取附近有约
     */
    @RequestMapping(value = "getNearbyDating", method = RequestMethod.POST)
    public
    @ResponseBody
    Response getNearbyDating(Long nId,String lng,String lat) throws Exception {
        return new ObjectResponse<>(nearbyDatingService.getNearbyDatingById(nId,lng,lat));
    }

    /**
     * 查询附近有约
     */
    @RequestMapping(value = "queryNearbyDating", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryNearbyDating(String lng,String lat,Integer page,Integer limit) throws Exception {
        return new ObjectResponse<>(nearbyDatingService.queryNearbyDatingByDistance(lng,lat,page,limit));
    }

    /**
     * 查询所有附近有约
     */
    @RequestMapping(value = "queryAllNearbyDating", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllNearbyDating(String keyword,Integer state,Integer page,Integer limit) throws Exception {
        return new ObjectResponse<>(nearbyDatingService.queryAllNearbyDating(keyword,state,page,limit));
    }

    /**
     * 查询附近约报名明细
     */
    @RequestMapping(value = "queryNearbyDatingApplyItem", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryNearbyDatingApplyItem(Long nId,Integer page,Integer limit) throws Exception {
        return new ObjectResponse<>(nearbyDatingService.queryNearbyDatingApplyItem(nId,page,limit));
    }

    /**
     * 查询用户发布的附近有约
     */
    @RequestMapping(value = "queryMyNearbyDating", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMyNearbyDating(Integer page,Integer limit) throws Exception {
        return new ObjectResponse<>(nearbyDatingService.queryUserNearbyDating(UserContext.getUserId(),true,page,limit));
    }

    /**
     * 查询用户报名的附近有约
     */
    @RequestMapping(value = "queryApplyNearbyDating", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryApplyNearbyDating(Integer page,Integer limit) throws Exception {
        return new ObjectResponse<>(nearbyDatingService.queryUserApplyNearbyDating(UserContext.getUserId(),page,limit));
    }

}
