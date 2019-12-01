package com.war4.base;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.war4.enums.CommonErrorResult;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.util.MD5Util;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dly on 2016/8/12.
 */
public class BaseController {
    private static List<String> datingCache = Collections.synchronizedList(new ArrayList<>());

    private static final String opPassword = "2c42e5cf1cdbafea04ed267018ef1511";

    protected void checkPassword(String password){
        if (!opPassword.equals(MD5Util.encode(password))){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"你无访问权限，请联系技术人员！");
        }
    }

    protected void addCache(String value){
        for (String s : datingCache) {
            if (s.equals(value)){
                //存在则为重复调用
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"操作过于频繁！");
            }
        }
        datingCache.add(value);
    }
    protected void removeCache(String value){
        datingCache.remove(value);
    }

    @ExceptionHandler
    public @ResponseBody Response runtimeExceptionHandler(Exception ex) throws Exception {
        ex.printStackTrace();
        // 只过滤定义异常
        if (ex instanceof BusinessException) {
            return new Response((BusinessException) ex);
        } else if (ex instanceof StaleObjectStateException ||
                ex instanceof DataAccessResourceFailureException ||
                ex instanceof CommunicationsException) {
            return new Response(CommonErrorResult.BUSINESS_ERROR, "网络繁忙");
        }
        return new Response(CommonErrorResult.BUSINESS_ERROR,"逻辑错误");
    }

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected KouDianYingService kouDianYingService;
    @Autowired
    protected UserFeedbackService feedbackService;
    @Autowired
    protected UserInfoBaseService userInfoBaseService;
    @Autowired
    protected UserInformantCenterService informantCenterService;
    @Autowired
    protected DemandRequestService demandRequestService;
    @Autowired
    protected UserShareRecordService userShareRecordService;
    @Autowired
    protected AppVersionService appVersionService;
    @Autowired
    protected ActivityBidService activityBidService;
    @Autowired
    protected BaseOrderService baseOrderService;
    @Autowired
    protected PayUtilService payUtilService;
    @Autowired
    protected AnchorRoomService anchorRoomService;
    @Autowired
    protected AnchorFanService anchorFanService;
    @Autowired
    protected ArticleService articleService;
    @Autowired
    protected CollectMappingService collectMappingService;
    @Autowired
    protected UserViewHistoryService userViewHistoryService;
    @Autowired
    protected AuctionService auctionService;
    @Autowired
    protected AuditService auditService;
    @Autowired
    protected ChatFriendService chatFriendService;
    @Autowired
    protected UserBlackService userBlackService;
    @Autowired
    protected MessageCenterService messageCenterService;
    @Autowired
    protected YinghezhongService yinghezhongService;
    @Autowired
    protected FilmOrderService filmOrderService;
    @Autowired
    protected SystemDictionaryService systemDictionaryService;
    @Autowired
    protected CouponService couponService;
    @Autowired
    protected UserCouponService userCouponService;
    @Autowired
    protected UserOtherCouponService userOtherCouponService;
    @Autowired
    protected CrewService crewService;
    @Autowired
    protected RecruitInfoService recruitInfoService;
    @Autowired
    protected RecruitApplyService recruitApplyService;
    @Autowired
    protected DatingSettingService datingSettingService;
    @Autowired
    protected DatingFilmService datingFilmService;
    @Autowired
    protected EvaluateSystemService evaluateSystemService;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected FilmService filmService;
    @Autowired
    protected FilmTopicService filmTopicService;
    @Autowired
    protected FilmCollectionService filmCollectionService;
    @Autowired
    protected FilmCommentService filmCommentService;
    @Autowired
    protected FilmRoomService filmRoomService;
    @Autowired
    protected GiftService giftService;
    @Autowired
    protected UserGiftService userGiftService;
    @Autowired
    protected MarketCouponService marketCouponService;
    @Autowired
    protected MarketOrderService marketOrderService;
    @Autowired
    protected MomentService momentService;
    @Autowired
    protected ProjectService projectService;
    @Autowired
    protected ProjectAppointmentService projectAppointmentService;
    @Autowired
    protected CouponRedeemCodeService couponRedeemCodeService;
    @Autowired
    protected TempValidCodeService tempValidCodeService;
    @Autowired
    protected ActorResumeService actorResumeService;
    @Autowired
    protected DirectorResumeService directorResumeService;
    @Autowired
    protected SeceenwriterResumeService seceenwriterResumeService;
    @Autowired
    protected ResumeWorItemService resumeWorItemService;
    @Autowired
    protected HomePageService homePageService;
    @Autowired
    protected ActivityService activityService;
    @Autowired
    protected SystemParameterService systemParameterService;
    @Autowired
    protected MessageSysService messageSysService;
    @Autowired
    protected UserAccountService userAccountService;
    @Autowired
    protected RechargeOrderService rechargeOrderService;
    @Autowired
    protected CashService cashService;
    @Autowired
    protected UserAccountDetailService userAccountDetailService;
    @Autowired
    protected UserCashAccountService userCashAccountService;
    @Autowired
    protected RongYunService rongYunService;
    @Autowired
    protected ShakeService shakeService;
    @Autowired
    protected UserCorrelativeService userCorrelativeService;
    @Autowired
    protected UserRecommendService userRecommendService;
    @Autowired
    protected UserPersonalDetailService userPersonalDetailService;
    @Autowired
    protected UserClientRecordService userClientRecordService;
    @Autowired
    protected UserVipSettingService userVipSettingService;
    @Autowired
    protected VideoChatService videoChatService;
    @Autowired
    protected BaseRepository baseRepository;
    @Autowired
    protected TaskListService taskListService;
    @Autowired
    protected NearbyDatingService nearbyDatingService;
    @Autowired
    protected AdPageService adPageService;
    @Autowired
    protected CommentSystemService commentSystemService;
    @Autowired
    protected AddressService addressService;

}
