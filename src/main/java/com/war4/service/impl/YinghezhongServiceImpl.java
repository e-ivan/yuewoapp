package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.*;
import com.war4.listener.event.FilmOrderEvent;
import com.war4.pojo.FilmOrder;
import com.war4.pojo.TaskList;
import com.war4.service.KouDianYingService;
import com.war4.service.TaskListService;
import com.war4.service.YinghezhongService;
import com.war4.util.*;
import com.war4.vo.Payload42VO;
import com.war4.vo.film.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hh on 2017.8.15 0015.
 */
@Service
@Slf4j
public class YinghezhongServiceImpl extends BaseService implements YinghezhongService {
    @Autowired
    private KouDianYingService kouDianYingService;
    @Autowired
    private TaskListService taskListService;

    public static final String CINEMA_MOVIE_COLLECTION = "CINEMA_MOVIE";

    @Override
    public List<YingCinemaVO> queryCinema() {
        return this.queryCinema(null);
    }

    @Override
    public List<YingCinemaVO> queryCinema(Integer state) {
        if (state != null) {
            return template.find(new Query(Criteria.where("state").is(state)), YingCinemaVO.class);
        }
        return template.findAll(YingCinemaVO.class);
    }

    @Override
    public CinemaVO getCinemaDetail(String cid) {
        return template.findOne(new Query(Criteria.where("cinemaId").is(cid)), CinemaVO.class);
    }

    @Override
    public Map<String, CinemaVO> queryCinemaDetailMap(Collection<String> cinemaIds) {
        List<CinemaVO> cinemas = template.find(new Query(Criteria.where("cinemaId").in(cinemaIds)), CinemaVO.class);
        return cinemas.stream().collect(Collectors.toMap(CinemaVO::getCinemaId, c -> c));
    }

    @Override
    public void updateCinemaRelation(CinemaVO cinema) {
        String cinemaId = cinema.getCinemaId();
        YingCinemaVO yingCinema = this.getCinema(cinemaId);
        if (yingCinema == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "cinemaId:" + cinemaId + " 的影院不存在！");
        }
        if (YingCinemaState.getByCode(cinema.getState()) == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "所选影院状态不存在！");
        }
        if (CinemaSource.getByCode(cinema.getSource()) == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "影院所属不存在！");
        }
        //设置城市信息
        CityVO cityVO = kouDianYingService.queryCityByName(cinema.getCityName());
        if (cityVO != null) {
//            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "数据库暂无[" + cinema.getCityName() + "]的城市信息，请联系技术人员！");
            cinema.setCityId(cityVO.getCityId());
            cinema.setCityCode(cityVO.getCityCode());
        } else {
            cinema.setCityId(0L);
            cinema.setCityCode("0");
        }
        cinema.setForeignCinemaId(cinema.getCinemaId());
        Update cinemaUpdate = MongoUtil.createUpdate(cinema, false);
        template.upsert(new Query(Criteria.where("cinemaId").is(cinemaId)), cinemaUpdate, CinemaVO.class);
        //更新原始数据
        yingCinema.setRelation(StringUtils.isNoneBlank(cinema.getCinemaAddress(), cinema.getCinemaTel(), cinema.getCityName(), cinema.getLatitude(), cinema.getLongitude()));
        yingCinema.setState(cinema.getState());
        yingCinema.setSource(cinema.getSource());
        Update yingCinemaUpdate = MongoUtil.createUpdate(yingCinema, false);
        template.updateFirst(new Query(Criteria.where("cinemaId").is(cinemaId)), yingCinemaUpdate, YingCinemaVO.class);
    }

    @Override
    public CutPage<YingCinemaVO> queryCinema(Integer page, Integer limit) {
        CutPage<YingCinemaVO> cutPage = new CutPage<>(page, limit);
        Query query = new Query();
        long count = template.count(query, YingCinemaVO.class);
        cutPage.setTotalCount(count);
        List<YingCinemaVO> cinemas = template.find(query.skip(cutPage.getOffset()).limit(cutPage.getLimit()), YingCinemaVO.class);
        cutPage.setiData(cinemas);
        return cutPage;
    }


    @Override
    public YingCinemaVO getCinema(String cid) {
        return template.findOne(new Query(Criteria.where("cinemaId").is(cid)), YingCinemaVO.class);
    }

    @Override
    public List<YingMovieInfoVO> queryCinemaMovie(String cid) {
        if (cid == null) {
            return template.findAll(YingMovieInfoVO.class, CINEMA_MOVIE_COLLECTION).stream().distinct().collect(Collectors.toList());
        }
        return template.find(new Query(Criteria.where("cinemaId").is(cid)), YingMovieInfoVO.class, CINEMA_MOVIE_COLLECTION);
    }

    @Override
    public CutPage<YingMovieInfoVO> queryMovie(Integer page, Integer limit) {
        CutPage<YingMovieInfoVO> cutPage = new CutPage<>(page, limit);
        Query query = new Query();
        long count = template.count(query, YingMovieInfoVO.class);
        cutPage.setTotalCount(count);
        query.with(new Sort(Sort.Direction.DESC, "created"));
        List<YingMovieInfoVO> movies = template.find(query.skip(cutPage.getOffset()).limit(cutPage.getLimit()), YingMovieInfoVO.class);
        cutPage.setiData(movies);
        return cutPage;
    }

    @Override
    public void updateMovieRelation(String cineMovieNum, Long movieId) {
        YingMovieInfoVO movieInfo = getMovieInfo(cineMovieNum);
        if (movieInfo == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "cineMovieNum:" + cineMovieNum + " 的电影不存在！");
        }
        MovieVO movie = kouDianYingService.getMovieInfo(movieId);
        if (movie == null) {//如果电影不存在，取消关联
            movieId = null;
        }
        //更新所有符合条件的
        template.updateMulti(new Query(Criteria.where("cineMovieNum").is(cineMovieNum)), new Update().set("kouMovieId", movieId), CINEMA_MOVIE_COLLECTION);
        template.updateMulti(new Query(Criteria.where("cineMovieNum").is(cineMovieNum)), new Update().set("kouMovieId", movieId), YingMovieInfoVO.class);
    }

    @Override
    public YingCinemaHallVO getCinemaHall(Long hallId) {
        return template.findOne(new Query(Criteria.where("id").is(hallId)), YingCinemaHallVO.class);
    }

    @Override
    public YingCinemaHallVO getCinemaHall(String cid, Long pid) {
        YingCinemaPlayVO cinemaPlay = template.findOne(new Query(Criteria.where("cinemaId").is(cid).and("planId").is(pid)), YingCinemaPlayVO.class);
        return this.getCinemaHall(cinemaPlay.getHallId());
    }


    @Override
    public YingCinemaHallSeatVO getCinemaHallSeat(String cid, Long cineSeatId) {
        return template.findOne(new Query(Criteria.where("cinemaId").is(cid).and("cineSeatId").is(cineSeatId)),
                YingCinemaHallSeatVO.class);
    }

    @Override
    public List<YingCinemaPlayVO> queryCinemaMoviePlay(String cid, Long mid, Date startDate, Integer amountDay) {
        //查询条件
        Criteria criteria = Criteria.where("cinemaId").is(cid).and("movieInfo.cineMovieId").is(mid);
        Date date = startDate != null ? startDate : new Date();
        String start = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        if (amountDay != null) {
            String end = DateFormatUtils.format(DateUtils.addDays(date, amountDay), "yyyy-MM-dd");
            criteria.and("startTime").gte(start).lt(end);
        } else {
            criteria.and("startTime").gte(start);
        }
        return template.find(new Query(criteria), YingCinemaPlayVO.class);
    }

    @Override
    public CutPage<YingCinemaPlayVO> queryCinemaMoviePlay(String cid, Long mid, Integer page, Integer limit) {
        CutPage<YingCinemaPlayVO> cutPage = new CutPage<>(page, limit);
        Criteria criteria = Criteria.where("cinemaId").is(cid).and("movieInfo.cineMovieId").is(mid);
        Query query = new Query(criteria);
        long count = template.count(query, YingCinemaPlayVO.class);
        cutPage.setTotalCount(count);
        List<YingCinemaPlayVO> plans = template.find(query.skip(cutPage.getOffset()).limit(cutPage.getLimit()), YingCinemaPlayVO.class);
        cutPage.setiData(plans);
        return cutPage;
    }

    @Override
    public YingMovieInfoVO getMovieInfo(String cineMovieNum) {
        return template.findOne(new Query(Criteria.where("cineMovieNum").is(cineMovieNum)), YingMovieInfoVO.class);
    }

    @Override
    public YingMovieInfoVO getMovieInfoByKouId(Long kouMovieId) {
        return template.findOne(new Query(Criteria.where("kouMovieId").is(kouMovieId)),YingMovieInfoVO.class);
    }

    @Override
    public List<CinemaVO> queryCinema(String cityCode, String movieId) {
        List<CinemaVO> cinemas = template.find(new Query(Criteria.where("source").is(CinemaSource.TIANZHI.getCode()).and("cityCode").is(cityCode)), CinemaVO.class);
        if (StringUtils.isNoneBlank(movieId)) {
            //查询城市中的影院
            List<String> cinemaIds = cinemas.stream().map(CinemaVO::getCinemaId).collect(Collectors.toList());
            //查询拥有该影片的影院
            List<String> hasMovieCinemaIds = template.find(new Query(Criteria.where("kouMovieId").is(Long.parseLong(movieId)).and("cinemaId").in(cinemaIds)), YingMovieInfoVO.class, CINEMA_MOVIE_COLLECTION)
                    .stream().map(YingMovieInfoVO::getCinemaId).collect(Collectors.toList());
            return cinemas.stream().filter(c -> hasMovieCinemaIds.contains(c.getCinemaId())).collect(Collectors.toList());
        }
        return cinemas;
    }

    @Override
    public YingCinemaPlayVO getPlayInfo(String cid, Long pid) {
        Criteria criteria = Criteria.where("cinemaId").is(cid).and("planId").is(pid);
        return template.findOne(new Query(criteria), YingCinemaPlayVO.class);
    }

    @Override
    public YingCinemaConfigVO getCinemaConfig(String cid) {
        return template.findOne(new Query(Criteria.where("cinemaId").is(cid)), YingCinemaConfigVO.class);
    }


    //获取订单原始数据
    @Override
    public YingOrderVO getFilmOrderHistory(String orderId) {
        return template.findOne(new Query(Criteria.where("orderId").is(orderId)), YingOrderVO.class);
    }

    @Override
    public void seatUnlock(String orderId) {
        YingOrderVO filmOrder = this.getFilmOrderHistory(orderId);
        if (filmOrder != null) {
            YingEntitytUtil.seatUnlock(filmOrder.getCinema().getCinemaId(), filmOrder.getPlan().getPlanId().toString(), filmOrder.getSeatLock().getLockFlag(), filmOrder.getSeatNo());
        }
    }

    @Override
    @Transactional
    public void confirmMovieOrder(String orderId) {
        //获取电影订单
        FilmOrder filmOrder = baseRepository.getObjById(FilmOrder.class, orderId);
        YingOrderVO filmOrderHistory = getFilmOrderHistory(orderId);

        //seat_id-handle_fee-price-user_real_pay_price-service_fee
        //要购买的座位-单张票总手续费-不包含手续费的影票价格（含服务费，即service_fee对应的值）-用户真实支付影票金额，不含手续费、服务费-影厅座位服务费
        List<String> list = new ArrayList<>();
        for (YingBuySeatVO buySeat : filmOrderHistory.getSeatLock().getSeats()) {
            StringBuilder seat = new StringBuilder("");
            seat.append(buySeat.getSeatId())//购买的座位
                    .append("-").append(buySeat.getHandleFee())//单张票总手续费
                    .append("-").append(buySeat.getPrice())//不包含手续费的影票价格
                    .append("-").append(buySeat.getUseRealPayPrice());//用户真实支付影票金额
            if (buySeat.getServiceFee() > 0) {
                seat.append("-").append(buySeat.getServiceFee());//影厅座位服务费
            }
            list.add(seat.toString());
        }

        //锁定座位并购买
        YingTicketVO yingTicketVO = YingEntitytUtil.seatLockBuy(filmOrderHistory.getCinema().getCinemaId(),
                filmOrderHistory.getPlan().getPlanId().toString(), filmOrderHistory.getPlan().getCineUpdateTime(),
                StringUtils.join(list, ","), filmOrderHistory.getSeatLock().getLockFlag(),
                orderId, filmOrder.getMobile());
        //设置订单信息
        filmOrder.setStartTime(DateUtil.parseDate(filmOrderHistory.getPlan().getStartTime(), "yyyy-MM-dd HH:mm:ss"));
        filmOrder.setEndTime(DateUtil.parseDate(filmOrderHistory.getPlan().getEndTime(), "yyyy-MM-dd HH:mm:ss"));
        filmOrder.setStatus(FilmOrderStatus.SUCCESS.getCode());
        baseRepository.saveObj(filmOrder);
        taskListService.createTask(new TaskList(filmOrder.getFkUserId(), filmOrder.getFkOrderId(), TaskListType.FILM_ORDER_REPORT, "影票上报天资"));
        //更订单状态
        updateFilmOrderVO(orderId, FilmOrderStatus.SUCCESS.getCode(), filmOrder.getMobile(), yingTicketVO.getTicketFlag1() + yingTicketVO.getTicketFlag2());
        //添加原始数据
        template.upsert(new Query(Criteria.where("orderId").is(orderId)), new Update().set("ticket", yingTicketVO), YingOrderVO.class);
        ac.publishEvent(new FilmOrderEvent(queryMovieOrderById(orderId)));//电影订单事件
    }

    @Override
    public void updateFilmOrderVO(String orderId, Integer status, String mobile, String ticketNo) {
        this.updateFilmOrderVO(orderId, status, mobile, ticketNo, null, null);
    }

    @Override
    public void updateFilmOrderVO(String orderId, Integer status, String mobile, String ticketNo, String finalTicketNo, String finalVerifyCode) {
        if (orderId != null && (status != null || StringUtils.isNotBlank(mobile) || StringUtils.isNotBlank(ticketNo) || StringUtils.isNoneBlank(finalTicketNo, finalTicketNo))) {
            Update update = new Update();
            if (status != null) {
                update.set("orderStatus", status);
            }
            if (StringUtils.isNotBlank(mobile)) {
                update.set("mobile", mobile);
            }
            if (StringUtils.isNotBlank(ticketNo)) {
                update.set("ticketNo", ticketNo);
                update.set("finalVerifyCode", ticketNo).set("finalTicketNo", ticketNo);
            } else {
                update.set("finalVerifyCode", finalVerifyCode).set("finalTicketNo", finalTicketNo);
            }
            template.updateFirst(new Query(Criteria.where("orderId").is(orderId)), update, FilmOrderVO.class);
        }
    }

    @Override
    public void updateFilmOrderVO(String orderId, Integer status) {
        this.updateFilmOrderVO(orderId, status, null, null);
    }

    @Override
    public List<YingOrderVO> querySaleFilmOrder(Payload42VO vo) {
        Criteria criteria = Criteria.where("ticket").exists(true);
        boolean startFlag = StringUtils.isNotBlank(vo.getStartBussinessDate());
        boolean endFlag = StringUtils.isNotBlank(vo.getEndBussinessDate());
        if (startFlag || endFlag) {
            criteria.and("plan.businessDate");
            if (startFlag) {
                criteria.gte(vo.getStartBussinessDate());//business_date
            }
            if (endFlag) {
                criteria.lte(vo.getEndBussinessDate());
            }
        }
        if (StringUtils.isNotBlank(vo.getCinemaCode())) {
            criteria.and("cinema.cinemaNumber").is(vo.getCinemaCode());//cinema_code
        }
        if (StringUtils.isNotBlank(vo.getScreenCode())) {
            criteria.and("hall.cineHallId").is(vo.getScreenCode());//screen_code
        }
        if (StringUtils.isNotBlank(vo.getFilmCode())) {
            criteria.and("movie.cineMovieNum").is(vo.getFilmCode());//film_code
        }
        if (StringUtils.isNotBlank(vo.getSessionCode())) {
            criteria.and("plan.cinePlayId").is(vo.getSessionCode());//session_code
        }
        if (StringUtils.isNotBlank(vo.getCode())) {
            criteria.and("ticket.sellInfo.sellId").is(vo.getCode());//code
        }
        return template.find(new Query(criteria), YingOrderVO.class);
    }

    @Override
    public void saveFilmOrderAndHistory(String cid, Long pid, String seatNo, FilmOrder filmOrder, YingSeatLockVO seatLock) {
        YingCinemaPlayVO playInfo = getPlayInfo(cid, pid);
        if (playInfo == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "参数异常！");
        }
        String[] seatNos = seatNo.split(",");
        List<YingCinemaHallSeatVO> list = new ArrayList<>();
        List<String> seatInfo = new ArrayList<>();
        for (String no : seatNos) {
            YingCinemaHallSeatVO cinemaHallSeat = getCinemaHallSeat(cid, Long.parseLong(no));
            seatInfo.add(cinemaHallSeat.getRow() + "_" + cinemaHallSeat.getColumn());
            list.add(cinemaHallSeat);
        }
        FilmOrderVO order = new FilmOrderVO();
        order.setMobile(filmOrder.getMobile());
        order.setCount(filmOrder.getCount());
        order.setSeatNo(seatNo);
        order.setMachineType("影院取票机");
        order.setMoney(filmOrder.getMoney().doubleValue());
        order.setOrderId(filmOrder.getFkOrderId());
        order.setOrderStatus(FilmOrderStatus.CREATE.getCode());
        order.setOrderTime(new Date());
        order.setPlanId(pid);
        order.setUnitPrice(filmOrder.getUnitPrice().doubleValue());
        order.setSeatInfo(StringUtils.join(seatInfo, ","));
        order.setPlan(transformPlan(playInfo));
        order.setUserId(filmOrder.getFkUserId());
        template.insert(order);
        /*
         * 保存天智创客订单原始数据
         */
        YingOrderVO vo = new YingOrderVO();
        vo.setOrderId(order.getOrderId());
        vo.setOrderTime(order.getOrderTime());
        vo.setCinema(getCinema(cid));
        vo.setCinemaConfig(getCinemaConfig(cid));
        vo.setHall(this.getCinemaHall(playInfo.getHallId()));
        vo.setMovie(playInfo.getMovieInfo().get(0));
        vo.setPlan(playInfo);
        vo.setSeatLock(seatLock);
        vo.setSeatNo(seatNo);
        vo.setSeats(list);
        template.insert(vo);
    }

    @Override
    public FilmOrderVO queryMovieOrderById(String orderId) {
        return template.findOne(new Query(Criteria.where("orderId").is(orderId)), FilmOrderVO.class);
    }

    @Override
    public int updateCinemas() {
        List<YingCinemaVO> yingCinemaVOS = YingEntitytUtil.partnerCinemas();
        List<YingCinemaVO> cinemaVOS = this.queryCinema(0, Integer.MAX_VALUE).getiData();
        Map<String, YingCinemaVO> map = cinemaVOS.stream().collect(Collectors.toMap(YingCinemaVO::getCinemaId, vo -> vo));
        List<YingCinemaVO> list = new ArrayList<>();
        yingCinemaVOS.forEach(vo -> {
            YingCinemaVO yingCinemaVO = map.get(vo.getCinemaId());
            if (yingCinemaVO != null) {
                vo.setSource(yingCinemaVO.getSource());
                vo.setRelation(yingCinemaVO.isRelation());
                vo.setState(yingCinemaVO.getState());
            }
            list.add(vo);
        });
        template.dropCollection(YingCinemaVO.class);
        template.insert(list, YingCinemaVO.class);
        return list.size();
    }

    @Override
    public int updateCinemaHalls() {
        List<YingCinemaVO> cinema = queryCinema();
        template.dropCollection(YingCinemaHallVO.class);
        int count = 0;
        for (YingCinemaVO yingCinemaVO : cinema) {
            List<YingCinemaHallVO> yingCinemaHallVOS = YingEntitytUtil.cinemaHalls(yingCinemaVO.getCinemaId());
            for (YingCinemaHallVO yingCinemaHallVO : yingCinemaHallVOS) {
                yingCinemaHallVO.setCinemaId(yingCinemaVO.getCinemaId());
            }
            template.insert(yingCinemaHallVOS, YingCinemaHallVO.class);
            count += yingCinemaHallVOS.size();
        }
        return count;
    }

    @Override
    public int updateCinemaHallSeats() {
        template.dropCollection(YingCinemaHallSeatVO.class);
        List<YingCinemaHallVO> cinemaHallVOS = template.findAll(YingCinemaHallVO.class);
        int count = 0;
        for (YingCinemaHallVO cinemaHallVO : cinemaHallVOS) {
            List<YingCinemaHallSeatVO> yingCinemaHallSeatVOS = YingEntitytUtil.queryCinemaHallSeats(cinemaHallVO.getCinemaId(), cinemaHallVO.getId().toString());
            template.insert(yingCinemaHallSeatVOS, YingCinemaHallSeatVO.class);
            count += yingCinemaHallSeatVOS.size();
        }
        return count;
    }

    @Override
    public int updateCinemaPlays() {
        List<YingCinemaVO> cinemaVOs = queryCinema();
        template.dropCollection(YingCinemaPlayVO.class);
        Long id = OrderUtil.createYingPlanId();//id记录
        int count = 0;
        for (YingCinemaVO cinemaVO : cinemaVOs) {
            YingCinemaConfigVO cinemaConfig = getCinemaConfig(cinemaVO.getCinemaId());
            List<YingCinemaPlayVO> playVOS = YingEntitytUtil.cinemaPlays(cinemaVO.getCinemaId(), cinemaConfig != null ? cinemaConfig.getPlayPeriod() : 10);
            for (YingCinemaPlayVO playVO : playVOS) {
                playVO.setPlanId(playVO.getId());
                playVO.setCinemaId(cinemaVO.getCinemaId());
                playVO.setId(id);
                id++;
            }
            template.insert(playVOS, YingCinemaPlayVO.class);
            count += playVOS.size();
        }
        return count;
    }

    @Override
    public List<YingMovieInfoVO> updateMovieInfo() {
        List<YingCinemaVO> cinemas = queryCinema();
        //每次更新删除原来数据
        template.dropCollection(CINEMA_MOVIE_COLLECTION);
        //影院上映电影
        List<YingMovieInfoVO> cinemaMovies = cinemas.stream().map(c -> {
            List<YingCinemaPlayVO> plans = template.find(new Query(Criteria.where("cinemaId").is(c.getCinemaId())), YingCinemaPlayVO.class);
            //将排期里的影院和电影数据分配好，影院 -> 电影
            List<YingMovieInfoVO> movieList = plans.stream().map(YingCinemaPlayVO::getMovieInfo).flatMap(ms -> ms.stream().map(m -> {
                m.setCinemaId(c.getCinemaId());
                m.setCineMovieNumToEquals(m.getCineMovieNum().substring(4, m.getCineMovieNum().length()));
                return m;
            })).distinct().collect(Collectors.toList());
            template.insert(movieList, CINEMA_MOVIE_COLLECTION);
            return movieList;
        }).flatMap(Collection::stream).distinct().collect(Collectors.toList());

        //将原来的关联数据保存回新数据中 （新增的电影才关联）
        Set<String> cineNums = cinemaMovies.stream().map(YingMovieInfoVO::getCineMovieNum).collect(Collectors.toSet());
        Collection<YingMovieInfoVO> movies = this.queryMovieByCineNums(cineNums);
        List<YingMovieInfoVO> newCineMovie = cinemaMovies.stream().filter(m -> {
            //筛选新电影
            m.setCreated(new Date());
            return movies.stream().noneMatch(m::equals);
        }).collect(Collectors.toList());
        template.insert(newCineMovie, YingMovieInfoVO.class);
        movies.forEach(m -> {
            if (m.getKouMovieId() != null) {
                template.updateMulti(new Query(Criteria.where("cineMovieNum").is(m.getCineMovieNum())), new Update().set("kouMovieId", m.getKouMovieId()), CINEMA_MOVIE_COLLECTION);
            }
        });
        return newCineMovie;
    }


    @Override
    public int updateCinemaConfig() {
        List<YingCinemaVO> cinema = queryCinema();
        template.dropCollection(YingCinemaConfigVO.class);
        for (YingCinemaVO yingCinemaVO : cinema) {
            YingCinemaConfigVO yingCinemaConfigVO = YingEntitytUtil.cinemaConfig(yingCinemaVO.getCinemaId());
            template.insert(yingCinemaConfigVO);
        }
        return cinema.size();
    }

    @Override
    public boolean autoRelationKouMovie(YingMovieInfoVO movieInfo, int[] count) {
        System.out.println("count = " + count[0]);
        //搜索电影的名字
        String movieName = movieInfo.getMovieName();
        //去掉括号内容
        movieName = StringUtils.substringBefore(StringUtils.substringBefore(movieName, "（"), "(");
        List<MovieVO> movieVOS = kouDianYingService.queryMovie(movieName, null, false, null, 0, CutPage.MAX_COUNT).getiData();
        if (movieVOS.size() < 1 && count[0] > 0) { //没有电影数据时更新数据再关联
            int i = kouDianYingService.updateMovie();
            --count[0];
            if (i > 0) { //当有电影数据更新时才执行
                this.autoRelationKouMovie(movieInfo, count);
            }
        } else if (movieVOS.size() == 1) { //只有一条记录时才进行自动关联
            updateMovieRelation(movieInfo.getCineMovieNum(), movieVOS.get(0).getMovieId());
            return true;
        }
        return false;
    }


    @Override
    public List<CinemaVO> transformCinema(Collection<YingCinemaVO> vos) {
        List<String> cinemaIds = vos.stream().map(YingCinemaVO::getCinemaId).collect(Collectors.toList());
        Map<String, CinemaVO> cinemaDetailMap = this.queryCinemaDetailMap(cinemaIds);
        return vos.stream().map(c -> {
            CinemaVO cinema = cinemaDetailMap.get(c.getCinemaId());
            if (cinema == null) {
                cinema = new CinemaVO();
                cinema.setCinemaId(c.getCinemaId());
                cinema.setCinemaName(c.getCinemaName());
            }
            return cinema;
        }).collect(Collectors.toList());
    }

    @Override
    public CinemaVO transformCinema(YingCinemaVO vo) {
        if (vo == null) {
            return new CinemaVO();
        }
        return this.transformCinema(Collections.singletonList(vo)).get(0);
    }

    @Override
    public List<MovieVO> transformMovie(Collection<YingMovieInfoVO> vos, boolean alterMovieId) {
        //获取有关联电影数据的id
        List<Long> kouMovieIds = vos.stream().filter(m -> m.getKouMovieId() != null).map(YingMovieInfoVO::getKouMovieId)
                .distinct().collect(Collectors.toList());
        Map<Long, MovieVO> movieMap = kouDianYingService.queryMovieMap(kouMovieIds);
        //转换数据
        return vos.stream().map(m -> {
            MovieVO movie = movieMap.get(m.getKouMovieId());
            if (movie == null) {
                //封装参数
                movie = new MovieVO();
                movie.setMovieName(m.getMovieName());
                movie.setMovieId(m.getCineMovieId());
            } else {
                movie.setRawMovieName(m.getMovieName());
            }
            if (alterMovieId) {
                movie.setMovieId(m.getCineMovieId());
            }
            movie.setCineMovieNum(m.getCineMovieNum());
            setMovieDimensional(movie, m);
            return movie;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MovieVO> transformMovie(Collection<YingMovieInfoVO> vos) {
        return this.transformMovie(vos, true);
    }

    @Override
    public MovieVO transformMovie(YingMovieInfoVO vo) {
        if (vo == null) {
            return new MovieVO();
        }
        return this.transformMovie(Collections.singletonList(vo)).get(0);
    }

    private static void setMovieDimensional(MovieVO movie, YingMovieInfoVO yingMovie) {
        movie.setHas3D(false);
        movie.setHas2D(false);
        movie.setHasImax(false);
        if (StringUtils.equalsIgnoreCase(yingMovie.getMovieDimensional(), "3D")) {
            movie.setHas3D(true);
        } else {
            movie.setHas2D(true);
        }
        if (StringUtils.equalsIgnoreCase("IMAX", yingMovie.getMovieSize())) {
            movie.setHasImax(true);
        }
    }

    @Override
    public List<CinemaPlanVO> transformPlan(Collection<YingCinemaPlayVO> vos) {
        //获取排期中的电影，并封装
        List<String> cineNums = vos.stream().map(YingCinemaPlayVO::getMovieInfo).flatMap(Collection::stream)
                .map(YingMovieInfoVO::getCineMovieNum).distinct().collect(Collectors.toList());
        List<YingMovieInfoVO> movieInfos = this.queryMovieByCineNums(cineNums);
        Map<String, MovieVO> movieMap = this.transformMovie(movieInfos).stream().distinct().collect(Collectors.toMap(MovieVO::getCineMovieNum, m -> m));
        //获取排期中的影院，并封装
        List<String> cinemaIds = vos.stream().map(YingCinemaPlayVO::getCinemaId).distinct().collect(Collectors.toList());
        List<YingCinemaVO> cinemas = this.queryCinemas(cinemaIds);
        Map<String, CinemaVO> cinemaMap = this.transformCinema(cinemas).stream().distinct().collect(Collectors.toMap(CinemaVO::getCinemaId, c -> c));
        //封装排期信息
        return vos.stream().map(p -> transformPlan(p, movieMap, cinemaMap)).collect(Collectors.toList());
    }

    @Override
    public CinemaPlanVO transformPlan(YingCinemaPlayVO vo) {
        if (vo == null) {
            return new CinemaPlanVO();
        }
        return this.transformPlan(Collections.singletonList(vo)).get(0);
    }

    @Override
    public List<HallSeatVO> transformSeat(Collection<YingPlaySeatStatusVO> vos, Long hallId, boolean onlyUnavailable) {
        List<HallSeatVO> list = new ArrayList<>();
        boolean loverFlag = true;//情侣座标记
        HallSeatVO hallSeatVO;
        for (YingPlaySeatStatusVO vo : vos) {
            int seatType = HallSeatType.getCodeByType(vo.getType());//座位类型
            int seatState = "ok".equals(vo.getSeatStatus()) ? 0 : 1;//座位状态
            if (onlyUnavailable && seatState == 0) {
                break;
            }
            if (seatType >= 0 && seatType < 4) {
                hallSeatVO = new HallSeatVO();
                hallSeatVO.setGraphCol(vo.getY());
                hallSeatVO.setGraphRow(vo.getX());
                hallSeatVO.setHallId(hallId);
                hallSeatVO.setSeatCol(vo.getColumnValue());
                hallSeatVO.setSeatRow(vo.getRowValue());
                hallSeatVO.setSeatNo(vo.getCineSeatId().toString());
                hallSeatVO.setSeatType(seatType);
                hallSeatVO.setSeatState(seatState);
                hallSeatVO.setSeatPieceNo(vo.getAreaId() == null ? null : vo.getAreaId().toString());
                if (seatType == 1) {//情侣座
                    hallSeatVO.setIsLoverL(loverFlag);
                    loverFlag = !loverFlag;
                }
                list.add(hallSeatVO);
            }
        }
        return list;
    }


    /**
     * 根据cineNum查询电影集合
     */
    private List<YingMovieInfoVO> queryMovieByCineNums(Collection<String> cineNums) {
        return template.find(new Query(Criteria.where("cineMovieNum").in(cineNums)), YingMovieInfoVO.class);
    }

    /**
     * 根据id获取影院
     */
    private List<YingCinemaVO> queryCinemas(Collection<String> cinemaIds) {
        return template.find(new Query(Criteria.where("cinemaId").in(cinemaIds)), YingCinemaVO.class);
    }

    /**
     * 封装排期信息
     */
    private static CinemaPlanVO transformPlan(YingCinemaPlayVO p, Map<String, MovieVO> movieMap, Map<String, CinemaVO> cinemaMap) {
        CinemaPlanVO plan = new CinemaPlanVO();
        YingMovieInfoVO movieInfo = p.getMovieInfo().get(0);
        MovieVO movie = movieMap.get(movieInfo.getCineMovieNum());
        if (movie == null) {
            return plan;
        }
        plan.setCinema(cinemaMap.get(p.getCinemaId()));
        plan.setMovie(movie);
        plan.setMovieId(movie.getMovieId());
        plan.setCinemaId(p.getCinemaId());
        plan.setPrice(FilmOrderPriceUtil.countFilmSalePrice(p));
        plan.setStandardPrice(p.getPrice());
        plan.setFeatureNo(p.getCinePlayId().toString());
        plan.setPlanId(p.getPlanId());
        plan.setFeatureTime(p.getStartTime());
        plan.setFeatureTimeDate(DateUtil.parseDate(p.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
        plan.setHallName(p.getHallName());
        plan.setHallNo(p.getHallId().toString());
        plan.setLanguage(movieInfo.getMovieLanguage());
        if (StringUtils.equalsIgnoreCase(movieInfo.getMovieDimensional(), "3D")) {
            plan.setScreenType("3D");
        } else {
            plan.setScreenType("2D");
        }
        return plan;
    }
}
