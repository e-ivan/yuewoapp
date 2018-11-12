package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.Film;
import com.war4.vo.film.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/26.
 */
public interface FilmService {
    Film saveFilm( String name, String place, Integer filmTime, String releaseTime, String intro, BigDecimal price) throws Exception;
    Film queryFilmById(String filmId);
    CutPage<Film> queryFilmList(Integer status,String name,Integer page ,Integer limit);
    void deleteFilmById(String filmId);
    void updateFilm(String id,Integer state);
    List<MovieVO> queryFilmByCityId(String cityId) throws Exception;
    //获取电影详情
    MovieVO queryFilmDetail(Long filmId) throws Exception;
    //获取即将上映影片
    List<MovieVO> queryComingFilm();
    //根据位置信息获取电影院列表排序

    /**
     * 根据城市和电影获取影院列表（按距离排序） cityId + movieId
     * 根据城市获取影院列表（按距离排序） cityId
     * @param cityId    定位城市
     * @param movieId   电影id
     * @param lng       用户经度
     * @param lat       用户纬度
     * @param other
     * @return
     */
    List<CinemaVO> orderByCinema(String cityId, String movieId, String lng, String lat, boolean other) throws Exception;
    //根据影院和影片获取指定排期
    List<CinemaPlanVO> queryFilmPlan(String cinemaId,String movieId,Date startDate) throws Exception;

    /**
     * 获取影厅座位信息
     * 同时传入planId + onlyUnavailable 或 hallId + cinemaId 获取座位信息
     * @param planId            排期id，与onlyUnavailable配对
     * @param onlyUnavailable   是否只返回不可用的座位
     * @param hallId            厅id，与cinemaId配对
     * @param cinemaId          影院id
     * @return
     */
    List<HallSeatVO> querySeat(String planId, boolean onlyUnavailable, String hallId,String cinemaId) throws Exception;
    //根据影院获取影片
    List<MovieVO> queryFilmByCinemaId(String cinemaId) throws Exception;

    //====================天智创客=======================
    //获取天智创客影院
    List<CinemaVO> queryYingCinema(String lng, String lat);
    //获取影院中的影片列表
    List<MovieVO> queryYingCinemaFilm(String cid);
    //根据影院和影片获取排期
    List<CinemaPlanVO> queryYingFilmPlan(String cid, Long mid, Date startDate,Integer amountDay);
    //获取影厅座位信息
    List<HallSeatVO> queryYingSeat(String cid, Long pid, boolean onlyUnavailable);
    //获取鼎新支持的影院
    CutPage<YingCinemaVO> queryYingCinema(Integer page,Integer limit);

    MovieVO queryMovieImage(String id);

    CinemaVO queryCinemaImage(String id);

    //查询所有影院
    List<CinemaVO> queryAllCinemas(Long cityId,String cinemaId,String keyword);

    //更新电影信息
    void updateMovieInfo(MovieVO movie, MultipartFile verticalImg, MultipartFile horizonImg);

    //使用mongodb索引删除电影信息
    void deleteMovie(Long id);

    Map<Long,MovieVO> queryMovieMap(Collection<Long> movieIds);
}
