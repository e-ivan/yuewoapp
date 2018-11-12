package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.enums.*;
import com.war4.pojo.Auction;
import com.war4.pojo.Film;
import com.war4.pojo.UserInfoBase;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.util.*;
import com.war4.vo.UserInHallSeatVO;
import com.war4.vo.film.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2016/12/26.
 */
@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private static final boolean INCLUDE_T_DATA = PropertiesConfig.isCombineFilmData();    //包含天智创客影院数据

    @Autowired
    private BaseRepository baseRepository;
    @Autowired
    private FileService fileService;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private KouDianYingService kouDianYingService;

    @Autowired
    private YinghezhongService yinghezhongService;

    @Autowired
    private UserInfoBaseService userInfoBaseService;

    @Autowired
    private AuctionService auctionService;
    @Autowired
    private MongoTemplate template;

    @Override
    @Transactional
    public Film saveFilm(String name, String place, Integer filmTime, String releaseTime, String intro, BigDecimal price) throws Exception {
        Film film = new Film();
        film.setId(UUID.randomUUID().toString());
        film.setName(name);
        film.setFilmTime(filmTime);
        film.setIntro(intro);
        film.setPlace(place);
        film.setPrice(price);
        film.setReleaseTime(releaseTime);

        film.setStatus(FilmStatus.SHELF.getCode());
        baseRepository.saveObj(film);


        return film;
    }

    @Override
    public Film queryFilmById(String filmId) {
        Film film = baseRepository.getObjById(Film.class, filmId);
        if (film == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到这个电影！");
        }
        film = assembleService.assembleObject(film);
        return film;
    }

    @Override
    public CutPage<Film> queryFilmList(Integer status, String name, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(Film.class);
        if (name != null) {
            hql += " and name like '%" + name + "%'";
        }
        if (status != null) {
            hql += " and status = " + status + " ";
        }
        CutPage<Film> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql, cutPage);
        for (Film film : cutPage.getiData()) {
            assembleService.assembleObject(film);
        }
        return cutPage;
    }

    @Override
    @Transactional
    public void deleteFilmById(String filmId) {
        Film film = baseRepository.getObjById(Film.class, filmId);
        if (film == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到这个电影！");
        }
        baseRepository.logicDelete(film);
    }

    @Override
    @Transactional
    public void updateFilm(String id, Integer state) {
        Film film = baseRepository.getObjById(Film.class, id);
        film.setStatus(state);
        baseRepository.updateObj(film);
    }

    @Override
    public List<MovieVO> queryFilmByCityId(String cityId) throws Exception {
//        CityVO city = this.queryKouCityIdByCityCode(cityId);
//        if (city != null) {
//            cityId = city.getCityId().toString();
//        }
//        if (!NumberUtil.isNumeric(cityId)) {
//            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "参数异常:cityId");
//        }
//        List<MovieVO> movies = kouDianYingService.queryMovieByCityId(cityId);
//        return this.handleMovieInfo(movies);
        return queryYingShowMovie();
    }

    private List<MovieVO> queryYingShowMovie() {
        List<Long> kmIds = template.findAll(YingMovieInfoVO.class, YinghezhongServiceImpl.CINEMA_MOVIE_COLLECTION)
                .stream().map(YingMovieInfoVO::getKouMovieId).distinct().collect(Collectors.toList());
        Map<Long, MovieVO> movieMap = kouDianYingService.queryMovieMap(kmIds);
        return new ArrayList<>(movieMap.values());
    }


    //如果不够四位则加上0
    private CityVO queryKouCityIdByCityCode(String cityCode) {
        if (StringUtils.length(cityCode) < 4) {
            cityCode = "0" + cityCode;
        }
        return template.findOne(new Query(Criteria.where("cityCode").is(cityCode)), CityVO.class);
    }

    private List<MovieVO> handleMovieInfo(List<MovieVO> movies) {
        List<Long> ids = movies.stream().map(MovieVO::getMovieId).collect(Collectors.toList());
        Map<Long, MovieVO> movieMap = this.queryMovieMap(ids);
        return movies.stream().map(m -> {
            MovieVO vo = movieMap.get(m.getMovieId());
            if (vo != null) {
                return vo;
            }
            return m;
        }).collect(Collectors.toList());
    }

    @Override
    public MovieVO queryFilmDetail(Long filmId) throws Exception {
        if (filmId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "参数异常:filmId");
        }
        return kouDianYingService.getMovieInfo(filmId);
    }

    @Override
    public List<MovieVO> queryComingFilm() {
        List<MovieVO> movies = kouDianYingService.queryComingFilm();
        return this.handleMovieInfo(movies);
    }

    @Override
    public List<CinemaVO> orderByCinema(String cityId, String movieId, String lng, String lat, boolean other) throws Exception {
        //获取天智创客影院
        CityVO city = this.queryKouCityIdByCityCode(cityId);
        List<CinemaVO> tCinemas = new ArrayList<>();
        if (city != null) {
            cityId = city.getCityId().toString();
            if (INCLUDE_T_DATA) {
                try {
                    tCinemas = yinghezhongService.queryCinema(city.getCityCode(), other ? null : movieId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (cityId == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "参数异常:cityId");
        }
        if ("0".equals(cityId)) {
            cityId = String.valueOf(kouDianYingService.queryCityByName(MapDistance.getCityName(lng, lat)).getCityId());
        }
        List<CinemaVO> cinemas = kouDianYingService.queryCinema(cityId, movieId);
        cinemas.addAll(tCinemas);
        return sortCinemaDistance(cinemas, lng, lat);
    }

    @Override
    public List<CinemaPlanVO> queryFilmPlan(String cinemaId, String movieId, Date startDate) throws Exception {
        List<CinemaPlanVO> filmPlan;
        if (KouDianYingServiceImpl.isKouCinemaId(cinemaId)) {
            filmPlan = kouDianYingService.queryPlanList(KouDianYingServiceImpl.disposeCinemaId(cinemaId), movieId);
            if (startDate != null) {
                filmPlan = filmPlan.stream().filter(c -> c.getFeatureTimeDate().after(startDate)).collect(Collectors.toList());
            }
        } else {
            /**
             * queryFilmByCinemaId返回的电影数据id是抠电影的，所以如果是天智创客影院是获取回电影所关联的具体id
             */
            YingMovieInfoVO movieInfo = yinghezhongService.getMovieInfoByKouId(Long.parseLong(movieId));
            filmPlan = this.queryYingFilmPlan(cinemaId, movieInfo != null ? movieInfo.getCineMovieId() : null, startDate, null);
        }
        return filmPlan;
    }

    @Override
    public List<HallSeatVO> querySeat(String planId, boolean onlyUnavailable, String hallId, String cinemaId) throws Exception {
        if (StringUtils.isNotEmpty(planId)) {
            if (INCLUDE_T_DATA && !KouDianYingServiceImpl.isKouCinemaId(cinemaId)) {//当需要包含天智创客数据时才路由
                return this.queryYingSeat(cinemaId,Long.parseLong(planId),onlyUnavailable);
            }
            //查询该排期中购买的电影票
            List<HallSeatVO> seatVOS = kouDianYingService.querySeatByPlan(planId, onlyUnavailable);
            try {
                List<FilmOrderVO> orderVOS = this.queryFilmOrderBySourceAndPlan(CinemaSource.KOU_DIAN_YING, Long.parseLong(planId));
                this.addSeatUserInfo(seatVOS, orderVOS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return seatVOS;
        }
        return kouDianYingService.querySeatByHall(hallId, cinemaId);
    }

    @Override
    public List<MovieVO> queryFilmByCinemaId(String cinemaId) throws Exception {
        if (StringUtils.isBlank(cinemaId)) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "参数异常:cinemaId");
        }
        if (KouDianYingServiceImpl.isKouCinemaId(cinemaId)) {
            return kouDianYingService.queryFilmByCinemaId(KouDianYingServiceImpl.disposeCinemaId(cinemaId));
        }
        return yinghezhongService.transformMovie(yinghezhongService.queryCinemaMovie(cinemaId),false);
    }

    //============================================================

    @Override
    public List<CinemaVO> queryYingCinema(String lng, String lat) {
        return sortCinemaDistance(yinghezhongService.transformCinema(yinghezhongService.queryCinema(YingCinemaState.BUSINESS.getCode())), lng, lat);
    }

    @Override
    public List<MovieVO> queryYingCinemaFilm(String cid) {
        return yinghezhongService.transformMovie(yinghezhongService.queryCinemaMovie(cid));
    }

    @Override
    public List<CinemaPlanVO> queryYingFilmPlan(String cid, Long mid, Date startDate, Integer amountDay) {
        List<YingCinemaPlayVO> vos = yinghezhongService.queryCinemaMoviePlay(cid, mid, startDate, amountDay);
        return yinghezhongService.transformPlan(vos);
    }

    @Override
    public List<HallSeatVO> queryYingSeat(String cid, Long pid, boolean onlyUnavailable) {
        YingCinemaPlayVO playInfo = yinghezhongService.getPlayInfo(cid, pid);
        if (playInfo != null) {
            List<YingPlaySeatStatusVO> yingPlaySeatStatusVOS = YingEntitytUtil.queryPlaySeatStatus(playInfo.getCinemaId(), pid.toString(), playInfo.getCineUpdateTime());
            List<HallSeatVO> hallSeatVOS = yinghezhongService.transformSeat(yingPlaySeatStatusVOS, playInfo.getHallId(), onlyUnavailable);
            List<FilmOrderVO> orderVOS = this.queryFilmOrderBySourceAndPlan(CinemaSource.TIANZHI, pid);
            this.addSeatUserInfo(hallSeatVOS, orderVOS);
            return hallSeatVOS;
        }
        return null;
    }

    @Override
    public CutPage<YingCinemaVO> queryYingCinema(Integer page, Integer limit) {
        return yinghezhongService.queryCinema(page, limit);
    }


    @Override
    @Transactional
    public MovieVO queryMovieImage(String id) {
        if (id == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "图片id不存在");
        }
        MovieVO movieVO = new MovieVO();
        movieVO.setMovieId(Long.parseLong(id));
        return assembleService.assembleObject(movieVO);
    }

    @Override
    @Transactional
    public CinemaVO queryCinemaImage(String id) {
        if (id == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "图片id不存在");
        }
        CinemaVO cinemaVO = new CinemaVO();
        cinemaVO.setCinemaId(id);
        return assembleService.assembleObject(cinemaVO);

    }

    @Override
    public List<CinemaVO> queryAllCinemas(Long cityId, String cinemaId, String keyword) {
        Criteria criteria = new Criteria();
        if (cityId != null) {
            String cityCode = cityId.toString();
            if (StringUtils.length(cityCode) < 4) {
                cityCode = "0" + cityCode;
            }
            criteria.and("cityCode").is(cityCode);
        }
        if (StringUtils.isNoneBlank(cinemaId)) {
            criteria.and("cinemaId").is(cinemaId);
        }
        if (StringUtils.isNoneBlank(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("cinemaName").regex(pattern);
        }
        return template.find(new Query(criteria), CinemaVO.class);
    }

    @Override
    public void updateMovieInfo(MovieVO movie, MultipartFile verticalImg, MultipartFile horizonImg) {
        if (movie.getMovieId() == null) {
            //新增操作
            movie.setMovieId(OrderUtil.createYingMovieId());
            movie.setSource("dingxin");
        }
        try {
            if (verticalImg != null) {
                List<String> list = fileService.saveFile(FileTypeAndPath.IMAGE_TYPE_PATH, FilePurpose.IMAGE_MOVIE, movie.getMovieId().toString(), verticalImg);
                movie.setPathVerticalS(list.get(0));
            }
            if (horizonImg != null) {
                List<String> list = fileService.saveFile(FileTypeAndPath.IMAGE_TYPE_PATH, FilePurpose.IMAGE_MOVIE, movie.getMovieId().toString(), horizonImg);
                movie.setPathHorizonH(list.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "上传图片失败");
        }
        Update update = MongoUtil.createUpdate(movie, false);
        template.upsert(new Query(Criteria.where("movieId").is(movie.getMovieId())), update, MovieVO.class);
    }

    @Override
    public void deleteMovie(Long id) {
        template.remove(new Query(Criteria.where("movieId").is(id)), MovieVO.class);
    }

    @Override
    public Map<Long, MovieVO> queryMovieMap(Collection<Long> movieIds) {
        List<MovieVO> movies = template.find(new Query(Criteria.where("movieId").in(movieIds)), MovieVO.class);
        return movies.stream().collect(Collectors.toMap(MovieVO::getMovieId, m -> m));
    }

    /**
     * 影院排序
     *
     * @param cinemas
     */
    private List<CinemaVO> sortCinemaDistance(List<CinemaVO> cinemas, String lng, String lat) {
        if (StringUtils.isBlank(lng) || StringUtils.isBlank(lat)) {
            return cinemas;
        }
        try {
            for (CinemaVO cinema : cinemas) {//计算并添加距离信息
                String longitude = cinema.getLongitude();
                String latitude = cinema.getLatitude();
                Double distance = 0D;
                if (StringUtils.isNotBlank(longitude) && StringUtils.isNotBlank(latitude)) {
                    distance = MapDistance.getDistance(lng, lat, longitude, latitude);
                }
                cinema.setDistance(distance);
            }
            cinemas.sort(Comparator.comparing(CinemaVO::getDistance));//按距离排序
            return cinemas;
        } catch (Exception e) {
            e.printStackTrace();
            return cinemas;
        }
    }

    private List<FilmOrderVO> queryFilmOrderBySourceAndPlan(CinemaSource source, Long planId) {
        Criteria criteria = Criteria.where("orderId").regex("\\b" + source.getValue() + ".{0,1}");
        criteria.and("orderStatus").is(FilmOrderStatus.SUCCESS.getCode());
        criteria.and("plan.planId").is(planId);
        return template.find(new Query(criteria), FilmOrderVO.class);
    }

    //添加座位用户信息
    private void addSeatUserInfo(List<HallSeatVO> seats, List<FilmOrderVO> orders) {
        //查询该排期中购买的电影票
        Map<String, String> userSeatMap = new HashMap<>();
        Set<String> userIds = new HashSet<>();
        orders.forEach(f -> {
            userIds.add(f.getUserId());
            for (String no : f.getSeatNo().split(",")) {
                userSeatMap.put(no, f.getUserId());
            }
        });
        //查询用户信息
        Map<String, UserInfoBase> userMap = userInfoBaseService.queryUserMap(userIds);
        //参与竞拍的用户
        List<String> auctionUserIds = userMap.values().stream().filter(UserInfoBase::getHasAuctionProcess).map(UserInfoBase::getId).collect(Collectors.toList());
        Map<String, Auction> auctionMap = auctionService.queryAuctionMapByUser(auctionUserIds);
        seats.forEach(s -> {
            String userId = userSeatMap.get(s.getSeatNo());
            //设置用户id
            if (userId != null) {
                UserInHallSeatVO vo = new UserInHallSeatVO();
                vo.setUserId(userId);
                //设置用户信息
                UserInfoBase user = userMap.get(userId);
                if (user != null) {
                    vo.setHeadImg(FileUploadUtils.getXSmallImage(user.getUserPhotoHead()));
                    vo.setAge(user.getAge());
                    vo.setNickname(user.getNickName());
                    vo.setSex(user.getSex());
                    Auction auction = auctionMap.get(userId);
                    if (auction != null) {
                        vo.setAuctionId(auction.getId());
                    }
                }
                s.setSeatUser(vo);
            }
        });
    }
}
