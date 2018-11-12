package com.war4.controller;

import com.alibaba.fastjson.JSON;
import com.war4.base.*;
import com.war4.enums.AppType;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.UserShareRecordType;
import com.war4.pojo.*;
import com.war4.util.CaptchaUtil;
import com.war4.util.MD5Util;
import com.war4.util.MongoUtil;
import com.war4.util.UserContext;
import com.war4.vo.AMapAdVO;
import com.war4.vo.CityDBVO;
import com.war4.vo.SuggestionVO;
import com.war4.vo.film.CinemaMenuVO;
import com.war4.vo.film.CityVO;
import com.war4.vo.film.MovieVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用的控制器
 * Created by hh on 2017.6.8 0008.
 */
@Controller
@RequestMapping(value = "common")
public class CommonController extends BaseController {

    private static final String RONG_YUN_LUCKY_MONEY_APP_SECRET = PropertiesConfig.getRongYunLuckyMoneyAppSecret();
    private static final String IOS_APP_VERSION = PropertiesConfig.getIosAppVersion();
    private static final List<MovieVO> MOVIE_VO_LIST = new ArrayList<>();

    //获取影院菜单
    @RequestMapping(value = "homeBar")
    public
    @ResponseBody
    Response homeBar(String version) throws Exception {
        StringBuilder sql = new StringBuilder(200);
        Map<String, Object> map = baseRepository.paramMap();
        sql.append("SELECT i1.* FROM system_dictionary_item i1 LEFT JOIN system_dictionary d ON(i1.parent_id = d.id)")
                .append(" WHERE d.sn = :sn AND i1.status = :status AND i1.`value` = ")
                .append("(SELECT MAX(i2.`value`) FROM system_dictionary_item i2 WHERE i2.parent_id = i1.parent_id AND i2.`value` <= :version) ");
        //如果是正在审核版本
        if (StringUtils.equalsIgnoreCase(version, IOS_APP_VERSION)) {
            sql.append(" AND i1.field != :field ");
            map.put("field", "1");
        }
        sql.append(" ORDER BY i1.sequence");
        map.put("sn", "homeBar");
        map.put("status", 1);
        map.put("version", parseAppVersion(version));
        List<SystemDictionaryItem> items = baseRepository.querySqlResult(sql, map, SystemDictionaryItem.class, 0, CutPage.MAX_COUNT);
        List<CinemaMenuVO> list = items.stream().map(i -> {
            CinemaMenuVO menu = JSON.parseObject(i.getExpression(), CinemaMenuVO.class);
            menu.setTitle(i.getTitle());
            return menu;
        }).collect(Collectors.toList());
        return new ObjectResponse<>(list);
    }

    private static Integer parseAppVersion(String appVersion) {
        String[] versionSub = StringUtils.split(appVersion, ".");
        if (versionSub != null) {
            if (versionSub.length == 3) {
                int one = Integer.parseInt(versionSub[0]) * 10000;
                int two = Integer.parseInt(versionSub[1]) * 100;
                int three = Integer.parseInt(versionSub[2]);
                return one + two + three;
            } else {
                return 0;
            }
        }
        return parseAppVersion(IOS_APP_VERSION);
    }

    //获取宣传页面
    @RequestMapping(value = "queryAdPage", method = {RequestMethod.POST, RequestMethod.GET})
    public
    @ResponseBody
    Response queryAdPage(Integer type) {
        return new ObjectResponse<>(adPageService.queryAdPage(type));
    }

    //登录测试
    @RequestMapping(value = "checkLogin")
    public
    @ResponseBody
    Response checkLogin() throws Exception {
        return new Response("登录成功！");
    }

    /**
     * 查询用户
     */
    @RequestMapping(value = "checkUser")
    public
    @ResponseBody
    Response checkUser(String userId) throws Exception {
        UserInfoBase user = userInfoBaseService.getUserSimpleById(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("exist", user != null);
        map.put("todayShare", userShareRecordService.checkShare(userId, PropertiesConfig.getVoteActivityId(), UserShareRecordType.BANNER.getCode()));
        return new ObjectResponse<>(map);
    }

    //根据城市中文名获取城市信息
    @RequestMapping(value = "queryCityByName", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryCityByName(String cityName) throws Exception {
        List<CityDBVO> cityList = addressService.queryCity(cityName);
        if (cityList.size() > 0) {
            return new ObjectResponse<>(transformCity(cityList.get(0)));
        } else {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "城市暂未提供影院数据");
        }
    }

    //获取所有省份信息
    @RequestMapping(value = "queryAllProvince", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllProvince(String keyword, Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(addressService.queryAllProvince(keyword, page, limit));
    }
    //获取所有城市信息
    @RequestMapping(value = "queryAllCity", method = RequestMethod.POST)
    public
    @ResponseBody
    Response queryAllCity(String provinceCode,String keyword, Integer page, Integer limit, boolean simple) throws Exception {
        CutPage<CityDBVO> cutPage = addressService.queryAllCity(provinceCode,keyword, page, limit);
        if (!simple) {
            return new ObjectResponse<>(cutPage);
        }
        List<SuggestionVO> collect = cutPage.getiData().stream().map(
                c -> new SuggestionVO(c.getCity(), c.getCityCode()))
                .collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("query", keyword);
        map.put("suggestions", collect);
        return new ObjectResponse<>(map);
    }

    private static CityVO transformCity(CityDBVO city) {
        CityVO vo = new CityVO();
        vo.setCityName(city.getCity());
        vo.setCityPinYin(city.getCityPinYin());
        vo.setLatitude(city.getLatitude());
        vo.setLongitude(city.getLongitude());
        vo.setCityId(Long.parseLong(city.getCityCode()));
        vo.setCityCode(city.getCityCode());
        return vo;
    }

    /**
     * 创建用户反馈
     */
    @RequestMapping(value = "createFeedback", method = RequestMethod.POST)
    public
    @ResponseBody
    Response createFeedback(UserFeedback userFeedback) throws Exception {
        feedbackService.createFeedback(userFeedback);
        return new Response("反馈成功");
    }

    /**
     * 创建用户举报
     */
    @RequestMapping(value = "createUserInformant", method = RequestMethod.POST)
    public
    @ResponseBody
    Response createUserInformant(UserInformantCenter userInformantCenter) throws Exception {
        informantCenterService.createInformant(userInformantCenter);
        return new Response("举报成功");
    }

    /**
     * 添加需求请求
     */
    @RequestMapping(value = "createDemandRequest", method = RequestMethod.POST)
    public
    @ResponseBody
    Response createDemandRequest(DemandRequest demandRequest) throws Exception {
        demandRequestService.createDemandRequest(demandRequest);
        return new Response("需求已提交！");
    }

    /**
     * 获取融云红包token
     *
     * @param userId
     */
    @RequestMapping(value = "getLuckyMoneyToken", method = RequestMethod.POST)
    public
    @ResponseBody
    Response getLuckyMoneyToken(String userId) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("luckyMoneyToken", MD5Util.encode(userId + RONG_YUN_LUCKY_MONEY_APP_SECRET));
        return new ObjectResponse<>(map);
    }

    /**
     * 保存分享
     */
    @RequestMapping(value = "saveUserShare", method = RequestMethod.POST)
    public
    @ResponseBody
    Response saveUserShare(String userId, String url, Integer type, String objectId) throws Exception {
        userShareRecordService.saveShare(userId, url, type, objectId);
        return new Response("分享成功");
    }

    /**
     * 服务器时间
     */
    @RequestMapping(value = "serverTime")
    public
    @ResponseBody
    Response serverTime() throws Exception {
        return new ObjectResponse<>(new Date());
    }

    /**
     * 获取商城地址
     */
    @RequestMapping(value = "shopServer")
    public
    @ResponseBody
    Response shopServer() throws Exception {
        return new ObjectResponse<>(PropertiesConfig.getShopUrl());
    }

    /**
     * 获取app升级
     */
    @RequestMapping(value = "appUpgrade")
    @ResponseBody
    public Response appVersion(Byte appType, Integer versionCode) throws Exception {
        return new ObjectResponse<>(appVersionService.selectLatestVersion(appType == null ? AppType.IOS_PHONE.getCode() : appType, versionCode == null ? 0 : versionCode));
    }

    /**
     * 上传软件
     */
    @RequestMapping(value = "uploadApp")
    @ResponseBody
    public Response uploadApp(MultipartFile file, VersionUpgrade version) throws Exception {
        appVersionService.saveVersion(file, version);
        return new Response("上传成功");
    }

    /**
     * 获取变量
     */
    @RequestMapping(value = "variable", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Response variable() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("auctionLowestPeriod", PropertiesConfig.getAuctionLowestPeriod());
        map.put("auctionFinish2FilmStartInterval", PropertiesConfig.getIntervalAuctionFinishWithFilmStartTime());
        map.put("shopServer", PropertiesConfig.getShopUrl());
        return new ObjectResponse<>(map);
    }

    /**
     * 添加评论
     */
    @RequestMapping(value = "addComment", method = {RequestMethod.POST})
    @ResponseBody
    public Response addComment(CommentSystem comment) throws Exception {
        comment.setUserId(UserContext.getUserId());
        commentSystemService.addComment(comment);
        return new Response("评论成功");
    }

    /**
     * 查询评论
     */
    @RequestMapping(value = "queryComment", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Response queryComment(Integer type, String objectId) throws Exception {
        List<CommentSystem> commentSystems = commentSystemService.queryComment(type, objectId, null);
        commentSystems.forEach(c -> c.setReplys(commentSystemService.queryComment(type, objectId, c.getId())));
        return new ObjectResponse<>(commentSystems);
    }

    /**
     * 删评论
     */
    @RequestMapping(value = "delComment", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Response delComment(Long commentId) throws Exception {
        commentSystemService.updateCommentState(commentId, false);
        return new Response("已删除");
    }

    //随机获取近期购买电影票用户
    @RequestMapping(value = "getRecommendMsg", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Response getRecommendMsg(String uid) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNoneBlank(uid)) {
            UserInfoBase detail = userInfoBaseService.getUserShareDetail(uid);
            if (detail != null) {
                map.put("user", JSON.parse("{\"recommendCode\":\"" + detail.getRecommendCode() + "\",\"headImg\":\"" + detail.getUserPhotoHead() + "\"}"));
            }
        }
        //处理过期的电影
        MOVIE_VO_LIST.removeIf(next -> next.getPublishTimeDate().before(DateUtils.addDays(new Date(), -30)));
        if (MOVIE_VO_LIST.size() < 5) {
            CutPage<MovieVO> cutPage = kouDianYingService.queryMovie(null, null, true, null, 0, CutPage.MAX_COUNT);
            System.out.println(cutPage.getDataCount());
            MOVIE_VO_LIST.addAll(cutPage.getiData());
        }
        //随机获取
        Collections.shuffle(MOVIE_VO_LIST);
        List<MovieVO> collect = MOVIE_VO_LIST.stream().limit(5).collect(Collectors.toList());
        List<String> msgs = collect.stream().map(m -> CaptchaUtil.buildRandomCodingPhoneChar() + " 刚获得《" + m.getMovieName() + "》电影票").collect(Collectors.toList());
        map.put("successOrder", msgs);
        return new ObjectResponse<>(map);
    }

    @RequestMapping(value = "uploadAddress", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Response uploadAddress(MultipartFile file) throws Exception {
        InputStream in = file.getInputStream();
        Workbook wb = WorkbookFactory.create(in);
        Sheet sheet = wb.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        List<AMapAdVO> list = new ArrayList<>();
        for (int i = 2; i < lastRowNum; i++) {
            Row row = sheet.getRow(i);
            Cell nameCell = row.getCell(0);
            Cell adCodeCell = row.getCell(1);
            Cell cityCodeCell = row.getCell(2);
            String name = nameCell.getStringCellValue();
            String adCode = adCodeCell.getStringCellValue();
            AMapAdVO aMapAdVO = new AMapAdVO(name, adCode, cityCodeCell != null ? cityCodeCell.getStringCellValue() : null);
            list.add(aMapAdVO);
        }
        int i = addressService.saveCityAddress(list);
        int j = addressService.saveProvinceAddress(list);
        return new Response(i > 0 ? "更新成功[" + i + "]条城市数据" + (j > 0 ? ",[" + j + "]条省份数据" : "") : "暂无新的城市数据");
    }

    /**
     * 临时使用更新影院信息
     * TODO 后续删除
     */
    @RequestMapping(value = "updateCinemas", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Response updateCinemas() throws Exception {
        List<MovieVO> all = mongoTemplate.findAll(MovieVO.class);
        all.forEach(m -> {
            try {
                MovieVO vo = kouDianYingService.queryMovieById(m.getMovieId().toString());
                vo.setSource("koudianying");
                mongoTemplate.updateMulti(new Query(Criteria.where("movieId").is(m.getMovieId())), MongoUtil.createUpdate(vo,true),MovieVO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return new Response();
    }

}
