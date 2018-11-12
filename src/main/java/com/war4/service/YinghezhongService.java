package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.FilmOrder;
import com.war4.vo.Payload42VO;
import com.war4.vo.film.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 天智创客服务
 * Created by hh on 2017.8.15 0015.
 */
public interface YinghezhongService {
    //====================获取鼎新转存在本地服务器中的数据======================
    //获取合作伙伴可以请求的影院的信息
    List<YingCinemaVO> queryCinema();
    List<YingCinemaVO> queryCinema(Integer state);
    //获取影院的详细信息
    CinemaVO getCinemaDetail(String cid);
    Map<String,CinemaVO> queryCinemaDetailMap(Collection<String> cinemaIds);
    //更新影院的详细信息
    void updateCinemaRelation(CinemaVO cinema);
    //获取所有影院
    CutPage<YingCinemaVO> queryCinema(Integer page,Integer limit);

    //根据id获取影院
    YingCinemaVO getCinema(String cid);

    //获取影厅上映电影列表,cid为null时返回所有上映电影
    List<YingMovieInfoVO> queryCinemaMovie(String cid);
    //获取所有电影
    CutPage<YingMovieInfoVO> queryMovie(Integer page,Integer limit);
    //更新电影中的关联信息
    void updateMovieRelation(String cineMovieNum, Long movieId);
    //获取电影信息
    YingMovieInfoVO getMovieInfo(String cineMovieNum);
    YingMovieInfoVO getMovieInfoByKouId(Long kouMovieId);
    //根据城市和电影获取影院
    List<CinemaVO> queryCinema(String cityCode,String movieId);

    //获取影厅
    YingCinemaHallVO getCinemaHall(Long hallId);
    YingCinemaHallVO getCinemaHall(String cid,Long pid);

    //获取座位信息
    YingCinemaHallSeatVO getCinemaHallSeat(String cid, Long cineSeatId);

    //获取影院影片场次
    List<YingCinemaPlayVO> queryCinemaMoviePlay(String cid, Long mid, Date startDate, Integer amountDay);
    CutPage<YingCinemaPlayVO> queryCinemaMoviePlay(String cid, Long mid, Integer page, Integer limit);


    //获取排期信息
    YingCinemaPlayVO getPlayInfo(String cid, Long pid);

    //获取影院配置信息
    YingCinemaConfigVO getCinemaConfig(String cid);

    //获取电影详细信息
    YingOrderVO getFilmOrderHistory(String orderId);

    //更新订单状态
    void updateFilmOrderVO(String orderId, Integer status, String mobile, String ticketNo);
    void updateFilmOrderVO(String orderId, Integer status, String mobile, String ticketNo, String finalTicketNo,String finalVerifyCode);
    void updateFilmOrderVO(String orderId, Integer status);

    //获取售出影票订单
    List<YingOrderVO> querySaleFilmOrder(Payload42VO vo);

    //=========================鼎新平台实时数据============================
    //座位解锁
    void seatUnlock(String orderId);
    //确认订单
    void confirmMovieOrder(String orderId);

    //保存天智创客电影订单和原始数据
    void saveFilmOrderAndHistory(String cid, Long pid, String seatNo, FilmOrder filmOrder, YingSeatLockVO seatLock);

    //获取电影订单
    FilmOrderVO queryMovieOrderById(String orderId);

    //========================定时获取鼎新中的数据保存在本地服务器================================
    //更新数据库影院
    int updateCinemas();
    //更新影厅信息
    int updateCinemaHalls();
    //更新影厅座位信息
    int updateCinemaHallSeats();
    //更新数据库影院排期
    int updateCinemaPlays();
    //更新数据库电影信息
    List<YingMovieInfoVO> updateMovieInfo();
    //更新影院配置信息
    int updateCinemaConfig();
    //自动关联电影数据
    boolean autoRelationKouMovie(YingMovieInfoVO movieInfo,int[] count);
    //=====================将鼎新数据转换为抠电影（通用）格式===========================
    //转换电影院
    List<CinemaVO> transformCinema(Collection<YingCinemaVO> vos);
    CinemaVO transformCinema(YingCinemaVO vo);

    /**
     * 转换电影
     * @param alterMovieId  需要改变关联的movieId为原cineMovieNum
     * @return
     */
    List<MovieVO> transformMovie(Collection<YingMovieInfoVO> vos,boolean alterMovieId);
    List<MovieVO> transformMovie(Collection<YingMovieInfoVO> vos);
    MovieVO transformMovie(YingMovieInfoVO vo);
    //转换场次
    List<CinemaPlanVO> transformPlan(Collection<YingCinemaPlayVO> vos);
    CinemaPlanVO transformPlan(YingCinemaPlayVO vo);
    //转换座位
    List<HallSeatVO> transformSeat(Collection<YingPlaySeatStatusVO> vos, Long hallId, boolean onlyUnavailable);
}
