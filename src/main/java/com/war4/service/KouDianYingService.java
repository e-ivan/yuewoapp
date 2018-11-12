package com.war4.service;


import com.war4.base.CutPage;
import com.war4.vo.film.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/12.
 */
public interface KouDianYingService {
    //获取电影详情
    MovieVO getMovieInfo(Long movieId);
    MovieVO queryMovieById(String id);
    //根据电影院id获取影院
    CinemaVO getCinemaById(String cinemaId);
    //查询支持的影院
    List<CinemaVO> queryCinema();

    List<MovieVO> queryMovieByCityId(String id);
    //获取即将上映电影
    List<MovieVO> queryComingFilm();
    //根据城市和影片获取影院列表
    List<CinemaVO> queryCinema(String cityId, String movieId);
    CinemaVO getCinema(String cinemaId);
    //根据影院和影片获取排期列表
    List<CinemaPlanVO> queryPlanList(String cinemaId, String movieId) throws Exception;
    //根据排期获取座位列表
    List<HallSeatVO> querySeatByPlan(String planId , boolean onlyUnavailable) throws Exception;
    //根据影厅信息获取座位列表
    List<HallSeatVO> querySeatByHall(String hallId,String cinemaId) throws Exception;
    //根据影院获取影片列表
    List<MovieVO> queryFilmByCinemaId(String cinemaId) throws Exception;

    //更新本地支持影院信息
    int updateCinemaChannel();
    //更新本地电影信息
    int updateMovie();
    //更新本地城市信息
    int updateCity();
    //获取本地数据库城市列表
    CutPage<CityVO> queryCity(String keyword, Integer page, Integer limit);
    //根据城市名称获取本地数据库中的城市信息
    CityVO queryCityByName(String cityName);
    CityVO queryCityById(String cityId);

    /**
     * 获取本地数据库储存的电影
     * @param keyword
     * @param publish
     * @param recent    最近的
     * @param movieId
     */
    CutPage<MovieVO> queryMovie(String keyword, Integer publish, boolean recent, Long movieId, Integer page, Integer limit);

    Map<Long,MovieVO> queryMovieMap(Collection<Long> movieIds);

}
