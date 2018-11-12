package com.war4.controller;

import com.alibaba.fastjson.JSON;
import com.war4.base.BaseController;
import com.war4.base.CutPage;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.enums.TaskListType;
import com.war4.pojo.SystemDictionaryItem;
import com.war4.pojo.TaskList;
import com.war4.util.DateUtil;
import com.war4.util.OrderUtil;
import com.war4.vo.SaleTicketVO;
import com.war4.vo.SuggestionVO;
import com.war4.vo.film.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 影院相关控制器
 * Created by hh on 2017.9.13 0013.
 */
@Controller
@RequestMapping(value = "cinema")
public class CinemaController extends BaseController {

    //获取影院菜单
    @RequestMapping(value = "menu")
    public
    @ResponseBody
    Response menu() {
        List<SystemDictionaryItem> cinemaMenu = systemDictionaryService.getSystemDictionaryBySN("cinemaMenu", true).getItems();
        List<CinemaMenuVO> list = new ArrayList<>();
        for (SystemDictionaryItem item : cinemaMenu) {
            CinemaMenuVO menu = JSON.parseObject(item.getExpression(), CinemaMenuVO.class);
            menu.setTitle(item.getTitle());
            list.add(menu);
        }
        return new ObjectResponse<>(list);
    }

    //更新数据库中的影院信息
    @RequestMapping(value = "updateCinema", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateCinema(String password) {
        checkPassword(password);
        Date begin = new Date();
        int i = yinghezhongService.updateCinemas();
        yinghezhongService.updateCinemaConfig();
        return new Response("更新成功[" + i + "]个影院信息，耗时" + DateUtil.intervalTime(begin, new Date()) + "秒！");
    }

    //更新抠电影影院信息
    @RequestMapping(value = "updateKouCinema", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateKouCinema(String password) {
        checkPassword(password);
        Date begin = new Date();
        int i = kouDianYingService.updateCinemaChannel();
        return new Response("更新成功[" + i + "]个影院信息，耗时" + DateUtil.intervalTime(begin, new Date()) + "秒！");
    }

    //更新数据库中的影院影厅、座位信息
    @RequestMapping(value = "updateCinemaHall", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateCinemaHall(String password) {
        checkPassword(password);
        Date begin = new Date();
        int halls = yinghezhongService.updateCinemaHalls();
        int seats = yinghezhongService.updateCinemaHallSeats();
        return new Response("更新成功[" + halls + "]个影厅信息和[" + seats + "]个座位信息，耗时" + DateUtil.intervalTime(begin, new Date()) + "秒！");
    }

    //更新数据库中的影院排期、电影信息
    @RequestMapping(value = "updateCinemaPlan", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateCinemaPlan(String password) {
        checkPassword(password);
        Date begin = new Date();
        int plays = yinghezhongService.updateCinemaPlays();
        List<YingMovieInfoVO> list = yinghezhongService.updateMovieInfo();
        int i = 0;
        if (list.size() > 0) {
            int[] count = {1};
            //自动更新电影信息
            List<YingMovieInfoVO> success = list.stream().filter(m -> yinghezhongService.autoRelationKouMovie(m, count)).collect(Collectors.toList());
            i = success.size();
        }
        return new Response("更新成功[" + plays + "]条影厅排期信息和[" + list.size() + "]条电影信息，并自动关联电影数据[" + i + "]条。耗时" + DateUtil.intervalTime(begin, new Date()) + "秒！");
    }

    //更新数据库中的抠电影的影片信息
    @RequestMapping(value = "updateKouMovie", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateKouMovie(String password) {
        checkPassword(password);
        Date begin = new Date();
        int movie = kouDianYingService.updateMovie();
        return new Response("更新成功[" + movie + "]条电影信息，耗时" + DateUtil.intervalTime(begin, new Date()) + "秒！");
    }

    //更新数据库中的抠电影的城市数据
    @RequestMapping(value = "updateKouCity", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateKouCity(String password) {
        checkPassword(password);
        Date begin = new Date();
        int city = kouDianYingService.updateCity();
        return new Response("更新成功[" + city + "]条城市数据，耗时" + DateUtil.intervalTime(begin, new Date()) + "秒！");
    }

    //获取影院详细信息
    @RequestMapping(value = "getYingCinemaDetail", method = RequestMethod.POST)
    public
    @ResponseBody
    Response getYingCinemaDetail(String cinemaId) {
        return new ObjectResponse<>(yinghezhongService.getCinemaDetail(cinemaId));
    }

    //更新影院详细信息
    @RequestMapping(value = "updateCinemaRelation", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateCinemaRelation(CinemaVO cinema) {
        yinghezhongService.updateCinemaRelation(cinema);
        return new Response("更新成功！");
    }

    //获取影院
    @RequestMapping(value = "queryCinema", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCinema(Long cityId, String keyword) {
        List<CinemaVO> cinemaVOS = filmService.queryAllCinemas(cityId, null, keyword);
        List<SuggestionVO> collect = cinemaVOS.stream().map(
                c -> new SuggestionVO(c.getCinemaName(), c.getCinemaId()))
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("query", keyword);
        map.put("suggestions", collect);
        return new ObjectResponse<>(map);
    }

    //获取天智创客支持的影片
    @RequestMapping(value = "queryYingMovie", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryYingMovie(Integer page, Integer limit) {
        return new ObjectResponse<>(yinghezhongService.queryMovie(page, limit));
    }

    //获取抠电影所有支持的影片
    @RequestMapping(value = "queryKouAllMovie", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryKouAllMovie(String keyword, Integer publish, Long movieId, boolean simple, Integer page, Integer limit) {
        CutPage<MovieVO> cutPage = kouDianYingService.queryMovie(keyword, publish, false, movieId, page == null ? 0 : page, limit == null ? CutPage.MAX_COUNT : limit);
        if (!simple) {
            return new ObjectResponse<>(cutPage);
        }
        List<SuggestionVO> collect = cutPage.getiData().stream().map(
                m -> new SuggestionVO(m.getMovieName(), m))
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("query", keyword);
        map.put("suggestions", collect);
        return new ObjectResponse<>(map);
    }

    //关联电影
    @RequestMapping(value = "relationMovie", method = RequestMethod.POST)
    public
    @ResponseBody
    Response relationMovie(String cineMovieNum, Long movieId) {
        yinghezhongService.updateMovieRelation(cineMovieNum, movieId);
        return new Response("更新成功");
    }

    //更新电影信息
    @RequestMapping(value = "updateMovieInfo", method = RequestMethod.POST)
    public
    @ResponseBody
    Response updateMovieInfo(MovieVO movie, MultipartFile verticalImg, MultipartFile horizonImg) {
        filmService.updateMovieInfo(movie, verticalImg, horizonImg);
        return new Response("更新成功");
    }

    //重置抠电影信息
    @RequestMapping(value = "resetMovieInfo", method = RequestMethod.POST)
    public
    @ResponseBody
    Response resetMovieInfo(Long movieId) {
        //获取抠电影数据
        MovieVO vo = kouDianYingService.queryMovieById(movieId.toString());
        filmService.updateMovieInfo(vo,null,null);
        return new Response("重置成功");
    }

    //删除电影信息（根据mongodb索引）
    @RequestMapping(value = "delMovieInfo", method = RequestMethod.POST)
    public
    @ResponseBody
    Response delMovieInfoById(Long id) {
        filmService.deleteMovie(id);
        return new Response("删除成功");
    }

    //获取影院电影排期
    @RequestMapping(value = "queryCinemaMoviePlan", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCinemaMoviePlan(String cinemaId, Long movieId, Integer page, Integer limit) {
        return new ObjectResponse<>(yinghezhongService.queryCinemaMoviePlay(cinemaId, movieId, page, limit));
    }

    @RequestMapping(value = "queryAllFilmOrderList", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllFilmOrderList(String src, Integer payType, String status, String keyword, Integer page, Integer limit) {
        CutPage cutPage = filmOrderService.queryAllOrderList(src, payType, status, keyword, page, limit);
        return new ObjectResponse<>(cutPage);
    }


    //电影订单详情
    @RequestMapping(value = "queryFilmOrderDetail", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryFilmOrderDetail(String orderId) {
        FilmOrderVO vo = yinghezhongService.queryMovieOrderById(orderId);
        Map<String, Object> filmOrderMap = new HashMap<>();
        //影院名称
        filmOrderMap.put("cinemaName", vo.getPlan().getCinema().getCinemaName());
        //影院ID
        filmOrderMap.put("cinemaId", vo.getPlan().getCinema().getCinemaId());
        //排期ID
        filmOrderMap.put("planId", vo.getPlan().getPlanId());
        //影厅名称
        filmOrderMap.put("hallName", vo.getPlan().getHallName());
        //影厅ID
        filmOrderMap.put("hallId", vo.getPlan().getHallNo());
        //电影名称，屏幕类型
        filmOrderMap.put("movieName", vo.getPlan().getMovie().getMovieName());
        filmOrderMap.put("screenType", vo.getPlan().getScreenType());
        //座位信息
        filmOrderMap.put("seatInfo", OrderUtil.parseSeatInfo(vo.getSeatInfo()));
        //播放时间,结束播放时间
        filmOrderMap.put("featureTime", vo.getPlan().getFeatureTime());
        filmOrderMap.put("finishTime", DateFormatUtils.format(
                DateUtils.addMinutes(
                        vo.getPlan().getFeatureTimeDate(), vo.getPlan().getMovie().getMovieLength())
                , "yyyy-MM-dd HH:mm:ss"));
        //取票信息
        filmOrderMap.put("ticketNo", vo.getFinalTicketNo());
        filmOrderMap.put("lng", vo.getPlan().getCinema().getLongitude());
        filmOrderMap.put("lat", vo.getPlan().getCinema().getLatitude());
        return new ObjectResponse<>(filmOrderMap);
    }

    @RequestMapping(value = "reportFilmOrder", method = RequestMethod.POST)
    public
    @ResponseBody
    Response reportFilmOrder(String password) {
        checkPassword(password);
        return new ObjectResponse<>(taskListService.queryTaskList(TaskList.CREATE, TaskListType.FILM_ORDER_REPORT, 0, CutPage.MAX_COUNT).getiData());
    }

    @RequestMapping(value = "reportSuccess", method = RequestMethod.POST)
    public
    @ResponseBody
    Response reportSuccess(String password, Integer count) {
        checkPassword(password);
        for (TaskList taskList : taskListService.queryTaskList(TaskList.CREATE, TaskListType.FILM_ORDER_REPORT, 0, count).getiData()) {
            taskListService.updateTaskFinish(taskList);
        }
        return new Response("设置成功");
    }

    @RequestMapping(value = "resetReport", method = RequestMethod.POST)
    public
    @ResponseBody
    Response resetReport(String password, @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,@DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        checkPassword(password);
        List<TaskList> taskLists = taskListService.queryTaskListByDate(TaskList.FINISH, TaskListType.FILM_ORDER_REPORT, startDate, endDate);
        taskLists.forEach(taskListService::restTask);
        String msg;
        if (taskLists.size() > 0) {
            msg = "提交成功" + taskLists.size() + "个订单数据";
        }else{
            msg = "该期间没有可重新上报的数据";
        }
        return new Response(msg);
    }

    @RequestMapping(value = "getFilmOrderHistory", method = RequestMethod.POST)
    @ResponseBody
    public Response getFilmOrderHistory(String orderId, String password) {
        checkPassword(password);
        YingOrderVO history = yinghezhongService.getFilmOrderHistory(orderId);
        List<SaleTicketVO> list = new ArrayList<>();
        if (history != null) {
            YingTicketVO ticket = history.getTicket();
            if (ticket != null) {
                YingCinemaPlayVO plan = history.getPlan();
                String screenCode = history.getHall().getCineHallId().toString();
                for (YingSellInfoVO sell : ticket.getSellInfo()) {
                    SaleTicketVO saleTicket = new SaleTicketVO();
                    saleTicket.setCinemaCode(history.getCinema().getCinemaNumber());
                    saleTicket.setBussinessDate(plan.getBusinessDate());
                    saleTicket.setCode(sell.getSellId());
                    saleTicket.setFilmCode(history.getMovie().getCineMovieNum());
                    saleTicket.setOnlineSaleCode("");
                    saleTicket.setOperation((byte) 0x01);
                    saleTicket.setSessionCode(plan.getCinePlayId().toString());
                    saleTicket.setScreenCode(screenCode);
                    saleTicket.setSeatCode(sell.getSeatId());
                    saleTicket.setPrice(Float.parseFloat(sell.getTicketIncome()));
                    saleTicket.setServiceFee((float) 0.0);
                    saleTicket.setSessionDateTime(plan.getStartTime().replace(" ", "T"));
                    list.add(saleTicket);
                }
            }
        }
        return new ObjectResponse<>(list);
    }

}
