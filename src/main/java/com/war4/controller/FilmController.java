package com.war4.controller;

import com.war4.base.*;
import com.war4.pojo.Film;
import com.war4.vo.film.CinemaPlanVO;
import com.war4.vo.film.CinemaVO;
import com.war4.vo.film.MovieVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/26.
 */
@Controller
@RequestMapping(value = "film")
@Slf4j
public class FilmController extends BaseController {

    private static final int DEF_AMOUNT_DAY = PropertiesConfig.getDefAmountDay();


    @RequestMapping(value = "uploadFilm", method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveFilm(String name, String place, Integer filmTime, String releaseTime, String intro, BigDecimal price) throws Exception {
        Film film = filmService.saveFilm(name, place, filmTime, releaseTime, intro, price);
        return new ObjectResponse<>(film);
    }

    //电影详情
    @RequestMapping(value = "queryFilmById", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmById(String filmId) throws Exception {
        Film film = filmService.queryFilmById(filmId);
        return new ObjectResponse<>(film);
    }

    //电影列表
    @RequestMapping(value = "queryFilmList", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmList(Integer status, String name, Integer page, Integer limit) throws Exception {
        CutPage cutPage = filmService.queryFilmList(status, name, page, limit);
        return new ObjectResponse<>(cutPage);
    }

    //删除电影
    @RequestMapping(value = "deleteFilmById", method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteFilmById(String filmId) throws Exception {
        filmService.deleteFilmById(filmId);
        return new Response("删除成功！");
    }

    @RequestMapping(value = "updateFilm", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateFilm(String id, Integer state) throws Exception {
        filmService.updateFilm(id, state);
        return new Response("操作成功！");
    }

    //城市电影列表
    @RequestMapping(value = "queryFilmByCityId", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmByCityId(String cityId) throws Exception {
        return new ObjectResponse<>(filmService.queryFilmByCityId(cityId));
    }

    //接口端电影详情
    @RequestMapping(value = "queryFilmDetail", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmDetail(Long filmId) throws Exception {
        return new ObjectResponse<>(filmService.queryFilmDetail(filmId));
    }

    //获取即将上映电影
    @RequestMapping(value = "queryComingFilm")
    public
    @ResponseBody
    Response queryComingFilm() throws Exception {
        return new ObjectResponse<>(filmService.queryComingFilm());
    }

    /**
     * 根据城市和电影获取影院列表（按距离排序）
     *
     * @param cityId  定位城市
     * @param movieId 电影id
     * @param lng     用户经度
     * @param lat     用户伟度
     * @return
     */
    @RequestMapping(value = "queryCinemaByMovie", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCinemaByMovie(String cityId, String movieId, String lng, String lat,boolean other) throws Exception {
        return new ObjectResponse<>(filmService.orderByCinema(cityId, movieId, lng, lat,other));
    }

    /**
     * 根据城市获取影院列表（按距离排序）
     *
     * @param cityId 定位城市
     * @param lng    用户经度
     * @param lat    用户伟度
     * @return
     */
    @RequestMapping(value = "queryCityCinema")
    public
    @ResponseBody
    Response queryCityCinema(String cityId, String lng, String lat) throws Exception {
        return new ObjectResponse<>(filmService.orderByCinema(cityId, null, lng, lat, false));
    }

    //根据影院和电影获取排期
    @RequestMapping(value = "queryFilmPlan", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmPlan(String cinemaId, String movieId, boolean auctionPlan) throws Exception {
        return new ObjectResponse<>(filmService.queryFilmPlan(cinemaId, movieId, this.getPlanStartDate(auctionPlan)));
    }

    //获取座位信息
    @RequestMapping(value = "querySeat", method = RequestMethod.POST)
    public
    @ResponseBody
    Response querySeat(String planId, boolean onlyUnavailable, String hallId, String cinemaId) throws Exception {
        return new ObjectResponse<>(filmService.querySeat(planId, onlyUnavailable, hallId, cinemaId));
    }

    //影院上映的电影列表
    @RequestMapping(value = "queryCinemaFilm", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCinemaFilm(String cinemaId) throws Exception {
        return new ObjectResponse<>(filmService.queryFilmByCinemaId(cinemaId));
    }


    //===================天智创客=======================

    /**
     * 获取影院
     *
     * @param lng 经度
     * @param lat 纬度
     */
    @RequestMapping(value = "queryYingCinema")
    public
    @ResponseBody
    Response queryYingCinema(Long cityId,String lng, String lat, Integer page, Integer limit) throws Exception {
        if (page != null) {
            return new ObjectResponse<>(filmService.queryYingCinema(page, limit));
        }
        return new ObjectResponse<>(filmService.queryYingCinema(lng, lat));
    }

    //获取影院影片列表
    @RequestMapping(value = "queryYingCinemaFilm", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryYingCinemaFilm(String cinemaId,boolean manage) throws Exception {
        List<MovieVO> movieVOS = filmService.queryYingCinemaFilm(cinemaId);
        if (PropertiesConfig.isProduction() && !manage) {
            //移除没有封面电影
            movieVOS.removeIf(next -> StringUtils.isBlank(next.getPathVerticalS()));
        }
        return new ObjectResponse<>(movieVOS);
    }

    //获取影院影片排期（场次）
    @RequestMapping(value = "queryYingFilmPlan", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryYingFilmPlan(String cinemaId, Long movieId, boolean auctionPlan,Integer amountDay) throws Exception {
        List<CinemaPlanVO> cinemaPlanVOS = filmService.queryYingFilmPlan(cinemaId, movieId,this.getPlanStartDate(auctionPlan), amountDay == null ? DEF_AMOUNT_DAY : amountDay);
        return new ObjectResponse<>(cinemaPlanVOS);
    }

    /**
     * 获取影院影片场次座位
     *
     * @param cinemaId
     * @param planId
     * @param onlyUnavailable 只返回不可用座位
     */
    @RequestMapping(value = "queryYingSeat", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryYingSeat(String cinemaId, Long planId, boolean onlyUnavailable) throws Exception {
        return new ObjectResponse<>(filmService.queryYingSeat(cinemaId, planId, onlyUnavailable));
    }

    @RequestMapping(value = "queryMovieImage", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryMovieImage(String id) throws Exception {

        MovieVO movieVO = filmService.queryMovieImage(id);

        return new ObjectResponse<>(movieVO);

    }

    @RequestMapping(value = "queryCinemaImage", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCinemaImage(String id) throws Exception {

        CinemaVO cinemaVO = filmService.queryCinemaImage(id);

        return new ObjectResponse<>(cinemaVO);

    }

    /**
     * 获取排期开始日期
     * @param auctionPlan
     * @return
     */
    private Date getPlanStartDate(boolean auctionPlan){
        Date startDate = null;
        if (auctionPlan) {
            //设置竞拍的排期时间
            startDate = DateUtils.addHours(new Date(), PropertiesConfig.getAuctionLowestPeriod() + PropertiesConfig.getIntervalAuctionFinishWithFilmStartTime());
        }
        return startDate;
    }

}
