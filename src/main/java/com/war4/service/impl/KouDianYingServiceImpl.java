package com.war4.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.war4.base.BaseService;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.enums.CinemaSource;
import com.war4.service.AddressService;
import com.war4.service.KouDianYingService;
import com.war4.util.NumberUtil;
import com.war4.util.RequestUtil;
import com.war4.vo.CityDBVO;
import com.war4.vo.film.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by Administrator on 2017/1/12.
 */
@Service
@Slf4j
public class KouDianYingServiceImpl extends BaseService implements KouDianYingService {

    @Autowired
    private AddressService addressService;

    private static final String CONN_URL = PropertiesConfig.getKouDianYingData();

    public static final String KOU_CINEMA_ID_PREFIX = "10000";

    private static final String KOU_CINEMA_ID_REGEX = "\\b" + KOU_CINEMA_ID_PREFIX + ".{0,5}";

    @Override
    public MovieVO getMovieInfo(Long movieId) {
        MovieVO vo = template.findOne(new Query(Criteria.where("movieId").is(movieId)), MovieVO.class);
        if (vo == null && movieId != null) {
            vo = this.queryMovieById(movieId.toString());
        }
        return vo;
    }

    @Override
    public MovieVO queryMovieById(String id) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "movie_Query");
        map.put("movie_id", id);
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        return JSONObject.parseObject(jsonObject.getString("movie"), MovieVO.class);
    }

    @Override
    public CinemaVO getCinemaById(String cinemaId) {
        return template.findOne(new Query(Criteria.where("cinemaId").is(cinemaId)), CinemaVO.class);
    }

    @Override
    public List<CinemaVO> queryCinema() {
        return template.find(new Query(Criteria.where("source").is(CinemaSource.KOU_DIAN_YING.getCode())), CinemaVO.class);
    }


    /**
     * 获取渠道支持的影院
     */
    private List<CinemaVO> queryCinemaChannel(Integer page, Integer count) throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "cinema_Channel");
        if (page != null) {
            map.put("page", page.toString());
        }
        if (count != null) {
            map.put("count", count.toString());
        }
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        List<CinemaVO> cinemas = JSON.parseArray(jsonObject.getString("cinemas"), CinemaVO.class);
        disposeCinemaList(cinemas);
        return cinemas;
    }

    @Override
    public List<MovieVO> queryMovieByCityId(String id) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "movie_Query");
        map.put("city_id", id);
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        return JSONArray.parseArray(jsonObject.getString("movies"), MovieVO.class);
    }

    //查询城市列表
    private List<CityVO> cityQuery() {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "city_Query");
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        return JSONArray.parseArray(jsonObject.getString("cities"), CityVO.class);
    }

    @Override
    public List<MovieVO> queryComingFilm() {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "movie_Query");
        map.put("coming", "1");
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        return JSONArray.parseArray(jsonObject.getString("movies"), MovieVO.class);
    }

    @Override
    public List<CinemaVO> queryCinema(String cityId, String movieId) {
        if (StringUtils.isNoneBlank(cityId, movieId)) {
            JSONObject jsonObject = queryKouCinema(cityId, movieId, null);
            List<CinemaVO> cinemas = JSONArray.parseArray(jsonObject.getString("cinemas"), CinemaVO.class);
            disposeCinemaList(cinemas);
            return cinemas;
        }
        return template.find(new Query(Criteria.where("cityId").is(Long.parseLong(cityId))), CinemaVO.class);
    }

    private static JSONObject queryKouCinema(String cityId, String movieId, String cinemaId) {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "cinema_Query");
        if (StringUtils.isNoneBlank(cityId)) {
            map.put("city_id", cityId);
        }
        if (StringUtils.isNoneBlank(movieId)) {
            map.put("movie_id", movieId);
        }
        if (StringUtils.isNoneBlank(cinemaId)) {
            String value = disposeCinemaId(cinemaId);
            map.put("cinema_id", value);
        }
        return RequestUtil.baseKouDianYingRequest(map, CONN_URL);
    }

    @Override
    public CinemaVO getCinema(String cinemaId) {
        JSONObject jsonObject = queryKouCinema(null, null, cinemaId);
        CinemaVO cinema = jsonObject.getObject("cinema", CinemaVO.class);
        disposeCinemaList(Collections.singletonList(cinema));
        return cinema;
    }

    @Override
    public List<CinemaPlanVO> queryPlanList(String cinemaId, String movieId) throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "plan_Query");
        map.put("cinema_id", disposeCinemaId(cinemaId));
        map.put("movie_id", movieId);
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        return JSONArray.parseArray(jsonObject.getString("plans"), CinemaPlanVO.class);
    }

    @Override
    public List<HallSeatVO> querySeatByPlan(String planId, boolean onlyUnavailable) throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "seat_Query");
        if (onlyUnavailable) {
            map.put("only_unavailable", "true");
        }
        map.put("plan_id", planId);
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        return JSONArray.parseArray(jsonObject.getString("seats"), HallSeatVO.class);
    }

    @Override
    public List<HallSeatVO> querySeatByHall(String hallId, String cinemaId) throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "seat_Query");
        map.put("cinema_id", disposeCinemaId(cinemaId));
        map.put("hall_id", hallId);
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        return JSONArray.parseArray(jsonObject.getString("seats"), HallSeatVO.class);
    }

    @Override
    public List<MovieVO> queryFilmByCinemaId(String cinemaId) throws Exception {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("action", "movie_Query");
        map.put("cinema_id", disposeCinemaId(cinemaId));
        JSONObject jsonObject = RequestUtil.baseKouDianYingRequest(map, CONN_URL);
        return JSONArray.parseArray(jsonObject.getString("movies"), MovieVO.class);
    }

    @Override
    public int updateCinemaChannel() {
        try {
            List<CinemaVO> cinemaVOS = this.queryCinemaChannel(1, CutPage.MAX_COUNT);
            List<CinemaVO> localVos = this.queryCinema();
            List<CinemaVO> newCinemas = cinemaVOS.stream()
                    .filter(o -> localVos.stream().noneMatch(x -> Objects.equals(x.getCinemaId(), o.getCinemaId())))
                    .map(c -> this.getCinema(c.getCinemaId())).collect(Collectors.toList());
            //查询影院数据
            template.insert(newCinemas, CinemaVO.class);
            return cinemaVOS.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updateMovie() {
        try {
            List<CityVO> citys = this.queryCity();
            //正在上映电影
            List<MovieVO> nowPublish = citys.stream().map(c -> this.queryMovieByCityId(c.getCityId() + "")).flatMap(Collection::stream).distinct().collect(Collectors.toList());
            List<MovieVO> futurePublish = this.queryComingFilm();
            //获取唯一的电影id
            List<Long> currentIds = Stream.of(nowPublish, futurePublish).flatMap(Collection::stream).distinct().map(MovieVO::getMovieId).collect(Collectors.toList());
            //查询已有电影数据
            List<MovieVO> movies = this.queryMovies(currentIds);
            //数据库没有保存的电影id
            List<Long> newIds = currentIds.stream().filter(i -> movies.stream().noneMatch(m -> m.getMovieId().equals(i))).collect(Collectors.toList());
            //查询所有电影数据
            List<MovieVO> movieList = newIds.stream().map(i -> {
                MovieVO vo = this.queryMovieById(i.toString());
                if (vo != null) {
                    vo.setSource("koudianying");
                }
                return vo;
            }).collect(Collectors.toList());
            template.insert(movieList, MovieVO.class);
            return movieList.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //根据id集合查询电影集合
    private List<MovieVO> queryMovies(Collection<Long> ids) {
        return template.find(new Query(Criteria.where("movieId").in(ids)), MovieVO.class);
    }

    @Override
    public int updateCity() {
        List<CityVO> citys = this.cityQuery();
        template.dropCollection(CityVO.class);
        //自动关联数据库中的城市
        citys.forEach(c -> {
            List<CityDBVO> dbCitys = addressService.queryCity(c.getCityName());
            if (dbCitys.size() > 0) {
                c.setCityCode(dbCitys.get(0).getCityCode());
            }
        });
        template.insert(citys, CityVO.class);
        return citys.size();
    }

    @Override
    public CutPage<CityVO> queryCity(String keyword, Integer page, Integer limit) {
        CutPage<CityVO> cutPage = new CutPage<>(page, limit);
        Query query = new Query();
        if (StringUtils.isNoneBlank(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where("cityName").regex(pattern), Criteria.where("cityPinYin").regex(pattern));
            query.addCriteria(criteria);
        }
        long count = template.count(query, CityVO.class);
        cutPage.setTotalCount(count);
        List<CityVO> citys = template.find(query.skip(cutPage.getOffset()).limit(cutPage.getLimit()), CityVO.class);
        cutPage.setiData(citys);
        return cutPage;
    }

    private List<CityVO> queryCity() {
        return this.queryCity(null, 0, CutPage.MAX_COUNT).getiData();
    }

    @Override
    public CityVO queryCityByName(String cityName) {
        return template.findOne(new Query(Criteria.where("cityName").is(cityName)), CityVO.class);
    }

    @Override
    public CityVO queryCityById(String cityId) {
        return template.findOne(new Query(Criteria.where("cityId").is(cityId)), CityVO.class);
    }

    @Override
    public CutPage<MovieVO> queryMovie(String keyword, Integer publish, boolean recent, Long movieId, Integer page, Integer limit) {
        CutPage<MovieVO> cutPage = new CutPage<>(page, limit);
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "publishTime"));
        if (StringUtils.isNoneBlank(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            Criteria criteria = new Criteria();
            long id = 0;
            if (NumberUtil.isNumeric(keyword)) {
                id = Long.parseLong(keyword);
            }
            criteria.orOperator(Criteria.where("movieName").regex(pattern), Criteria.where("actor").regex(pattern),
                    Criteria.where("director").regex(pattern), Criteria.where("intro").regex(pattern), Criteria.where("movieId").is(id));
            query.addCriteria(criteria);
        }
        if (publish != null) {
            String now = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
            Criteria criteria = Criteria.where("publishTime");
            switch (publish) {
                case 0:
                    criteria.gt(now);
                    query.with(new Sort(Sort.Direction.ASC, "publishTime"));
                    break;
                case 1:
                    criteria.lte(now);
                    break;
            }
            query.addCriteria(criteria);
        }
        if (movieId != null) {
            query.addCriteria(Criteria.where("movieId").is(movieId));
        }
        if (recent) {
            String start = DateFormatUtils.format(DateUtils.addDays(new Date(), -30), "yyyy-MM-dd");
            query.addCriteria(Criteria.where("publishTime").gte(start));
        }
        long count = template.count(query, MovieVO.class);
        cutPage.setTotalCount(count);
        List<MovieVO> movies = template.find(query.skip(cutPage.getOffset()).limit(cutPage.getLimit()), MovieVO.class);
        cutPage.setiData(movies);
        return cutPage;
    }

    @Override
    public Map<Long, MovieVO> queryMovieMap(Collection<Long> movieIds) {
        List<MovieVO> movies = template.find(new Query(Criteria.where("movieId").in(movieIds)), MovieVO.class);
        return movies.stream().collect(Collectors.toMap(MovieVO::getMovieId, m -> m));
    }

    //处理cinemaId
    public static String disposeCinemaId(String cinemaId) {
        if (isKouCinemaId(cinemaId)) {
            return cinemaId.substring(5);
        }
        return cinemaId;
    }

    public static boolean isKouCinemaId(String cinemaId) {
        return cinemaId != null && cinemaId.length() > 5 && cinemaId.matches(KOU_CINEMA_ID_REGEX);
    }

    //处理影院集合数据
    private void disposeCinemaList(List<CinemaVO> vos) {
        vos.forEach(c -> {
            c.setForeignCinemaId(c.getCinemaId());
            c.setCinemaId(KOU_CINEMA_ID_PREFIX + c.getCinemaId());
            c.setSource(CinemaSource.KOU_DIAN_YING.getCode());
            //保存数据库中的cityCode为cityId
            CityVO city = this.queryCityById(c.getCityId().toString());
            if (city != null) {
                c.setCityCode(city.getCityCode());
            }
        });
    }
}
