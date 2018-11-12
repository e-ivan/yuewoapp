package com.war4.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.war4.base.CutPage;
import com.war4.enums.*;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.util.UserContext;
import com.war4.vo.film.CinemaVO;
import com.war4.vo.film.FilmOrderVO;
import com.war4.vo.film.MovieVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by dly on 2016/9/14.
 */
@Service
public class AssembleServiceImpl implements AssembleService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private UserInfoBaseService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private UserGiftService userGiftService;

    @Autowired
    private ActorResumeService actorResumeService;

    @Autowired
    private DirectorResumeService directorResumeService;

    @Autowired
    private SeceenwriterResumeService seceenwriterResumeService;

    @Autowired
    private UserVIPService userVIPService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private KouDianYingService kouDianYingService;

    @Autowired
    private RecruitApplyService recruitApplyService;

    @Autowired
    private FilmRoomService filmRoomService;

    @Autowired
    private ResumeWorItemService resumeWorItemService;

    @Autowired
    private MomentService momentService;

    @Autowired
    private KouDianYingOrderService kouDianYingOrderService;

    @Autowired
    private YinghezhongService yinghezhongService;

    @Autowired
    private ActivityBidService activityBidService;

    @Autowired
    private AuctionService auctionService;

    @Override
    public <T> T assembleObject(T obj) {
        if (obj == null) {//为空不组装
            return null;
        }
        if (obj.getClass().getName().equals(UserInfoBase.class.getName())) {
            return (T) assembleUser((UserInfoBase) obj);
        }
        if (obj.getClass().getName().equals(HomePage.class.getName())) {
            return (T) assembleHomePage((HomePage) obj);
        }
        if (obj.getClass().getName().equals(Activity.class.getName())) {
            return (T) assembleActivity((Activity) obj);
        }
        if (obj.getClass().getName().equals(Gift.class.getName())) {
            return (T) assembleGift((Gift) obj);
        }
        if (obj.getClass().getName().equals(UserGift.class.getName())) {
            return (T) assembleUserGift((UserGift) obj);
        }
        if (obj.getClass().getName().equals(DatingSetting.class.getName())) {
            return (T) assembleDatingSetting((DatingSetting) obj);
        }
        if (obj.getClass().getName().equals(DatingFilm.class.getName())) {
            return (T) assembleDatingFilm((DatingFilm) obj);
        }
        if (obj.getClass().getName().equals(ActorResume.class.getName())) {
            return (T) assembleActorResume((ActorResume) obj);
        }
        if (obj.getClass().getName().equals(DirectorResume.class.getName())) {
            return (T) assembleDirectorResume((DirectorResume) obj);
        }
        if (obj.getClass().getName().equals(SeceenwriterResume.class.getName())) {
            return (T) assembleSeceenwriterResume((SeceenwriterResume) obj);
        }
        if (obj.getClass().getName().equals(ResumeWorItem.class.getName())) {
            return (T) assembleResumeWorItem((ResumeWorItem) obj);
        }
        if (obj.getClass().getName().equals(Crew.class.getName())) {
            return (T) assembleCrew((Crew) obj);
        }
        if (obj.getClass().getName().equals(RecruitApply.class.getName())) {
            return (T) assembleRecruitApply((RecruitApply) obj);
        }
        if (obj.getClass().getName().equals(Project.class.getName())) {
            return (T) assembleProject((Project) obj);
        }
        if (obj.getClass().getName().equals(Film.class.getName())) {
            return (T) assembleFilm((Film) obj);
        }
        if (obj.getClass().getName().equals(FilmOrder.class.getName())) {
            return (T) assembleFilmOrder((FilmOrder) obj);
        }
        if (obj.getClass().getName().equals(AnchorRoom.class.getName())) {
            return (T) assembleAnchorRoom((AnchorRoom) obj);
        }
        if (obj.getClass().getName().equals(ChatFriend.class.getName())) {
            return (T) assembleChatFriend((ChatFriend) obj);
        }
        if (obj.getClass().getName().equals(FilmRoom.class.getName())) {
            return (T) assembleFilmRoom((FilmRoom) obj);
        }
        if (obj.getClass().getName().equals(FilmRoomUser.class.getName())) {
            return (T) assembleFilmRoomUser((FilmRoomUser) obj);
        }
        if (obj.getClass().getName().equals(FilmTopic.class.getName())) {
            return (T) assembleFilmTopic((FilmTopic) obj);
        }
        if (obj.getClass().getName().equals(FilmTopicComment.class.getName())) {
            return (T) assembleFilmTopicComment((FilmTopicComment) obj);
        }
        if (obj.getClass().getName().equals(FilmComment.class.getName())) {
            return (T) assembleFilmComment((FilmComment) obj);
        }
        if (obj.getClass().getName().equals(UserBlack.class.getName())) {
            return (T) assembleUserBlack((UserBlack) obj);
        }
        if (obj.getClass().getName().equals(UserCoupon.class.getName())) {
            return (T) assembleUserCoupon((UserCoupon) obj);
        }
        if (obj.getClass().getName().equals(UserVipSetting.class.getName())) {
            return (T) assembleUserVipSetting((UserVipSetting) obj);
        }
        if (obj.getClass().getName().equals(RecruitInfo.class.getName())) {
            return (T) assembleRecruitInfo((RecruitInfo) obj);
        }
        if (obj.getClass().getName().equals(MessageFriendApply.class.getName())) {
            return (T) assembleMessageFriendApply((MessageFriendApply) obj);
        }
        if (obj.getClass().getName().equals(Trailer.class.getName())) {
            return (T) assembleTrailer((Trailer) obj);
        }
        if (obj.getClass().getName().equals(UserRealName.class.getName())) {
            return (T) assembleUserRealName((UserRealName) obj);
        }
        if (obj.getClass().getName().equals(MovieVO.class.getName())) {
            return (T) assembleKouMovie((MovieVO) obj);
        }
        if (obj.getClass().getName().equals(CinemaVO.class.getName())) {
            return (T) assembleCinema((CinemaVO) obj);
        }
        if (obj.getClass().getName().equals(EnrollForm.class.getName())) {
            return (T) assembleEnrollForm((EnrollForm) obj);
        }
        if (obj.getClass().getName().equals(ActivityBid.class.getName())) {
            return (T) assembleActivityBid((ActivityBid) obj);
        }
        if (obj.getClass().getName().equals(Auction.class.getName())) {
            return (T) assembleAuction((Auction) obj);
        }


        return obj;
    }


    private FilmOrder assembleFilmOrder(FilmOrder filmOrder) {
        String fkOrderId = filmOrder.getFkOrderId();
        //先查询mongodb中的电影订单数据
        FilmOrderVO orderVO = yinghezhongService.queryMovieOrderById(fkOrderId);
        if (orderVO == null) {
            if (fkOrderId.contains("a")) {//抠电影
                try {
                    //不存在则查询外网记录
                    List<FilmOrderVO> filmOrderVOS = kouDianYingOrderService.queryMovieOrderById(fkOrderId, null);
                    filmOrder.setJsonArray(JSON.parseArray(JSONArray.toJSONString(filmOrderVOS)));
                    return filmOrder;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //天智创客
        filmOrder.setJsonArray(JSON.parseArray(JSONArray.toJSONString(Collections.singletonList(orderVO))));
        return filmOrder;
    }

    private UserRealName assembleUserRealName(UserRealName userRealName) {
        List<FileUpload> albumList = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.REAL_NAME.getCode(),
                userRealName.getId(),
                CutPage.MAX_COUNT).getiData();
        if (albumList != null && albumList.size() > 0) {
            userRealName.setRealNameList(albumList);
        }
        if (albumList == null) {
            userRealName.setRealNameList(new ArrayList<>());
        }
        return userRealName;
    }

    private Trailer assembleTrailer(Trailer trailer) {

        if (trailer == null) {
            return null;
        }
        String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.TRAILER_PHOTO.getCode(),
                trailer.getId());
        trailer.setTrailerPhoto(photoUrl);
        return trailer;
    }

    //基本账户对象参数封装
    private UserInfoBase assembleUser(UserInfoBase userInfoBase) {
        if (userInfoBase == null) {
            return null;
        }
        UserAccessToken ut = baseRepository.getObjById(UserAccessToken.class, userInfoBase.getId());
        userInfoBase.setUserAccessToken(ut);
        List<FileUpload> albumList = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.IMAGE_ALBUM.getCode(),
                userInfoBase.getId(),
                CutPage.MAX_COUNT).getiData();
        if (albumList != null) {
            userInfoBase.setAlbumList(albumList);
        }
        return userInfoBase;
    }

    private HomePage assembleHomePage(HomePage homePage) {
        if (homePage == null) {
            return null;
        }
        String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.IMAGE_HOME_PAGE.getCode(),
                homePage.getId());
        homePage.setPhotoUrl(photoUrl);

        String sharePhotoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.IMAGE_HOME_PAGE_SHARE.getCode(),
                homePage.getId());
        homePage.setSharePicUrl(sharePhotoUrl);

        String popPhotoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.IMAGE_HOME_PAGE_POP.getCode(),
                homePage.getId());
        homePage.setPopUpPicUrl(popPhotoUrl);

        return homePage;
    }

    private Activity assembleActivity(Activity activity) {
        if (activity == null) {
            return null;
        }
        String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.IMAGE_ACTIVITY.getCode(),
                activity.getId());
        activity.setPhotoUrl(photoUrl);
        return activity;
    }

    private Gift assembleGift(Gift gift) {
        if (gift == null) {
            return null;
        }
        String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.IMAGE_GIFT.getCode(),
                gift.getId());
        gift.setPhotoUrl(photoUrl);
        return gift;
    }

    private UserGift assembleUserGift(UserGift userGift) {
        if (userGift == null) {
            return null;
        }
        userGift.setGift(giftService.queryGiftById(userGift.getFkGiftId()));
        return userGift;
    }

    private DatingSetting assembleDatingSetting(DatingSetting datingSetting) {
        if (datingSetting != null) {
            datingSetting.setGift(giftService.queryGiftById(datingSetting.getFkGiftId()));
        }
        return datingSetting;
    }

    private DatingFilm assembleDatingFilm(DatingFilm datingFilm) {
        if (datingFilm != null) {
            datingFilm.setCreateUser(userService.getUserSimpleById(datingFilm.getCreateUserId()));
            datingFilm.setAcceptUser(userService.getUserSimpleById(datingFilm.getAcceptUserId()));
        }
        return datingFilm;
    }

    private ActorResume assembleActorResume(ActorResume actorResume) {
        if (actorResume != null) {
            String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.ACTOR_RESUME_COVER_PHOTO.getCode(),
                    actorResume.getFkUserId());
            actorResume.setCoverPhoto(photoUrl);
            List<FileUpload> photoWallList = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.ACTOR_RESUME_PHOTO_WALL.getCode(),
                    actorResume.getFkUserId(),
                    CutPage.MAX_COUNT).getiData();
            if (photoWallList != null && photoWallList.size() > 0) {
                actorResume.setPhotoWall(photoWallList);
            }

        }
        return actorResume;
    }

    private DirectorResume assembleDirectorResume(DirectorResume directorResume) {
        if (directorResume != null) {
            String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.DIRECTOR_RESUME_COVER_PHOTO.getCode(),
                    directorResume.getFkUserId());
            directorResume.setCoverPhoto(photoUrl);
            List<FileUpload> photoWallList = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.DIRECTOR_RESUME_PHOTO_WALL.getCode(),
                    directorResume.getFkUserId(),
                    CutPage.MAX_COUNT).getiData();
            if (photoWallList != null && photoWallList.size() > 0) {
                directorResume.setPhotoWall(photoWallList);
            }

            directorResume.setCount(resumeWorItemService.queryResumeWorItemSum(directorResume.getFkUserId(), ResumeWorkType.DIRECTOR.getCode()));
        }
        return directorResume;
    }

    private SeceenwriterResume assembleSeceenwriterResume(SeceenwriterResume seceenwriterResume) {
        if (seceenwriterResume != null) {
            String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.SECEENWRITER_RESUME_COVER_PHOTO.getCode(),
                    seceenwriterResume.getFkUserId());
            seceenwriterResume.setCoverPhoto(photoUrl);
            List<FileUpload> photoWallList = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.SECEENWRITER_RESUME_PHOTO_WALL.getCode(),
                    seceenwriterResume.getFkUserId(),
                    CutPage.MAX_COUNT).getiData();
            if (photoWallList != null && photoWallList.size() > 0) {
                seceenwriterResume.setPhotoWall(photoWallList);
            }
            seceenwriterResume.setCount(resumeWorItemService.queryResumeWorItemSum(seceenwriterResume.getFkUserId(), ResumeWorkType.SECEENWRITER.getCode()));

        }
        return seceenwriterResume;
    }

    private ResumeWorItem assembleResumeWorItem(ResumeWorItem resumeWorItem) {
        if (resumeWorItem != null) {
            String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.RESUME_ITEM_PHOTO.getCode(),
                    resumeWorItem.getId());
            resumeWorItem.setCoverPhoto(photoUrl);
        }
        return resumeWorItem;
    }

    private Crew assembleCrew(Crew crew) {
        if (crew != null) {
            String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.CREW_PHOTO.getCode(),
                    crew.getId());
            crew.setBackPhoto(photoUrl);
        }
        return crew;
    }

    private RecruitApply assembleRecruitApply(RecruitApply recruitApply) {
        if (recruitApply != null) {
            if (RecruitType.ACOTER.getCode().equals(recruitApply.getApplyType())) {
                recruitApply.setActorResume(actorResumeService.queryActorResume(recruitApply.getFkUserId()));
            }
            if (RecruitType.DIRECTOR.getCode().equals(recruitApply.getApplyType())) {
                recruitApply.setDirectorResume(directorResumeService.queryDirectorResumeById(recruitApply.getFkUserId()));
            }
            if (RecruitType.SECEENWRITER.getCode().equals(recruitApply.getApplyType())) {
                recruitApply.setSeceenwriterResume(seceenwriterResumeService.querySeceenwriterResumeForId(recruitApply.getFkUserId()));
            }
        }
        return recruitApply;
    }

    private Project assembleProject(Project project) {
        if (project != null) {
            String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.PROJECT_PHOTO.getCode(),
                    project.getId());
            project.setProjectPhoto(photoUrl);

            List<FileUpload> photoWallList = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.PROJECT_DETAIL_PHOTO.getCode(),
                    project.getId(),
                    CutPage.MAX_COUNT).getiData();
            if (photoWallList != null && photoWallList.size() > 0) {
                project.setDetailPhotos(photoWallList);
            }
        }
        return project;
    }

    private Film assembleFilm(Film film) {
        if (film != null) {
            String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.FILM_PHOTO.getCode(),
                    film.getId());
            film.setPhotoUrl(photoUrl);

            String filmUrl = getCommonPhotoUrl(FileTypeAndPath.VIDEO_TYPE_PATH.getType(),
                    FilePurpose.FILM_VIDEO.getCode(),
                    film.getId());
            film.setFilmUrl(filmUrl);
        }
        return film;
    }

    private AnchorRoom assembleAnchorRoom(AnchorRoom anchorRoom) {
        if (anchorRoom != null) {
            List<FileUpload> anchorPhoto = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.IMAGE_ANCHOR_PHOTO.getCode(),
                    anchorRoom.getFkUserId(),
                    1).getiData();
            if (anchorPhoto != null && anchorPhoto.size() > 0) {
                anchorRoom.setPhotoUrl(anchorPhoto.get(0).getLocation());
            }
            anchorRoom.setUserInfoBase(userService.getUserById(anchorRoom.getFkUserId()));
        }

        return anchorRoom;
    }

    private ChatFriend assembleChatFriend(ChatFriend chatFriend) {
        if (chatFriend != null) {
            UserInfoBase userInfoBase = userService.getUserSimpleById(chatFriend.getFkFriendUserId());
            chatFriend.setUserInfoBase(userInfoBase);
//            if(chatFriend.getNote()==null){
//                chatFriend.setNote(userInfoBase.getNickName());
//            }
        }
        return chatFriend;
    }

    private FilmRoom assembleFilmRoom(FilmRoom filmRoom) {
        if (filmRoom != null) {
            filmRoom.setFilm(filmService.queryFilmById(filmRoom.getFkFilmId()));
            filmRoom.setRoomPerson(filmRoomService.queryFilmRoomUsersCount(filmRoom.getId()));
        }
        return filmRoom;
    }

    private FilmRoomUser assembleFilmRoomUser(FilmRoomUser filmRoomUser) {
        if (filmRoomUser != null) {
            filmRoomUser.setUser(userService.getUserById(filmRoomUser.getFkUserId()));
        }
        return filmRoomUser;
    }

    private FilmTopic assembleFilmTopic(FilmTopic filmTopic) {
        if (filmTopic != null) {
            filmTopic.setUser(userService.getUserSimpleById(filmTopic.getFkUserId()));

            /*
            List<FileUpload> photoWallList = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.FILM_TOPIC_PHOTO.getCode(),
                    filmTopic.getId(),
                    CutPage.MAX_COUNT).getiData();
            if (photoWallList != null && photoWallList.size() > 0) {
                filmTopic.setPhotoList(photoWallList);
            }
            */
            filmTopic.setPhotoList(Collections.emptyList());//TODO 因为我们暂时没有保存自己的电影，所以设置为空集
            filmTopic.setMovie(new MovieVO());
            try {
                //filmTopic.setMovie(kouDianYingService.queryMovieById(filmTopic.getFkFilmId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filmTopic;
    }

    private FilmTopicComment assembleFilmTopicComment(FilmTopicComment filmTopicComment) {

        if (filmTopicComment != null) {
            filmTopicComment.setUser(userService.getUserSimpleById(filmTopicComment.getFkUserId()));
        }
        return filmTopicComment;
    }

    private FilmComment assembleFilmComment(FilmComment filmComment) {
        if (filmComment != null) {
            filmComment.setUser(userService.getUserById(filmComment.getFkUserId()));
            try {
                filmComment.setMovie(kouDianYingService.queryMovieById(filmComment.getFkFilmId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filmComment;
    }

    private UserBlack assembleUserBlack(UserBlack userBlack) {
        if (userBlack != null) {
            userBlack.setBlackUser(userService.getUserById(userBlack.getBlackUserId()));
        }
        return userBlack;
    }

    //TODO 耗性能的地方，每次查询都要重新获取数据库中两张表的数据再计算时间，可以直接使用冗余数据代替
    private UserCoupon assembleUserCoupon(UserCoupon userCoupon) {
        Coupon coupon = baseRepository.getObjById(Coupon.class, userCoupon.getFkCouponId());
        if (coupon != null) {//如果每次查询出来的时间不同则更新
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(userCoupon.getCreateTime());
            calendar.add(Calendar.DATE, coupon.getPrescription());
            if (userCoupon.getOffTime() == null || !userCoupon.equals(calendar.getTime())) {
                userCoupon.setOffTime(calendar.getTime());
                baseRepository.updateObj(userCoupon);
            }
        }
        userCoupon.setCoupon(coupon);

        return userCoupon;
    }

    private UserVipSetting assembleUserVipSetting(UserVipSetting userVipSetting) {

        userVipSetting.setCoupon(baseRepository.getObjById(Coupon.class, userVipSetting.getFkCouponId()));

        return userVipSetting;
    }


    private RecruitInfo assembleRecruitInfo(RecruitInfo recruitInfo) {

        if (recruitInfo != null) {
            CutPage cutPage = recruitApplyService.queryRecruitApplyList(recruitInfo.getId(), 0, CutPage.MAX_COUNT);
            recruitInfo.setApplySum(cutPage.getTotalCount().intValue());
        }

        return recruitInfo;
    }

    private MessageFriendApply assembleMessageFriendApply(MessageFriendApply messageFriendApply) {

        if (messageFriendApply != null) {
            messageFriendApply.setFromUser(userService.getUserById(messageFriendApply.getFkFromUserId()));
        }

        return messageFriendApply;
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------
    private String getCommonPhotoUrl(String fileType, String filePurpose, String fkPurposeId) {
        List<FileUpload> photoList = fileService.getFilesByConditions(fileType,
                filePurpose, fkPurposeId, 1).getiData();
        if (photoList != null && photoList.size() > 0) {
            return photoList.get(0).getLocation();
        }
        return null;
    }

    private MovieVO assembleKouMovie(MovieVO movieVO) {
        if (movieVO == null) {
            return null;
        }
        String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.IMAGE_KOU_MOVIE.getCode(),
                movieVO.getMovieId().toString());

        movieVO.setPathVerticalS(photoUrl);

        return movieVO;
    }

    //
    private CinemaVO assembleCinema(CinemaVO cinemaVO) {
        if (cinemaVO == null) {
            return null;
        }
        String photoUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.IMAGE_CINEMA.getCode(),
                cinemaVO.getCinemaId());

        cinemaVO.setLogo(photoUrl);

        return cinemaVO;
    }

    private EnrollForm assembleEnrollForm(EnrollForm enrollForm) {
        if (enrollForm == null) {
            return null;
        }
        List<FileUpload> albumList = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.USER_ENROLL_FORM_ALBUM.getCode(),
                enrollForm.getUserId(),
                CutPage.MAX_COUNT).getiData();
        if (albumList != null && albumList.size() > 0) {
            enrollForm.setAlbumList(albumList);
        }
        if (albumList == null) {
            enrollForm.setAlbumList(new ArrayList<>());
        }

        String idCardUrl = getCommonPhotoUrl(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                FilePurpose.USER_ID_CARD_PHOTO.getCode(),
                enrollForm.getUserId());
        if (idCardUrl != null) {
            enrollForm.setIdCardUrl(idCardUrl);
        }

        return enrollForm;
    }

    private ActivityBid assembleActivityBid(ActivityBid activityBid) {
        List<ActivityBidItem> items;
        int joinState = ActivityBidJoinState.VIABLE.getCode();
        if (activityBid.getState().equals(ActivityBid.STATE_OVER)) {//活动结束
            joinState = ActivityBidJoinState.FINISH.getCode();
            //查询中奖明细
            items = activityBidService.queryActivityBidItemList(activityBid.getId(), ActivityBidItem.VICTOR, 0, CutPage.MAX_COUNT).getiData();
            for (ActivityBidItem item : items) {
                if (item.getUserId().equals(activityBid.getCurrentUser())) {
                    joinState = ActivityBidJoinState.VICTOR.getCode();
                    break;
                }
            }
        } else {
            items = activityBidService.queryActivityBidItemList(activityBid.getId(), null, true, 0, CutPage.MAX_COUNT).getiData();
            if (activityBid.getState().equals(ActivityBid.STATE_FULLPEOPLE) || activityBid.getState().equals(ActivityBid.STATE_GOING)) {//活动满人
                for (ActivityBidItem item : items) {
                    if (item.getUserId().equals(activityBid.getCurrentUser())) {
                        //当订单为未付款是可以参与
                        if (ActivityBid.STATE_CREATE.equals(item.getState())) {
                            break;
                        } else if (item.getCurrentCount() > 1) {//自动参与
                            joinState = ActivityBidJoinState.SYSTEM_JOIN.getCode();
                        } else {//手动参与
                            joinState = ActivityBidJoinState.MANUAL_JOIN.getCode();
                        }
                        break;
                    } else {
                        if (activityBid.getState().equals(ActivityBid.STATE_FULLPEOPLE)) {
                            //否则已满员
                            joinState = ActivityBidJoinState.FULL.getCode();
                        }
                    }
                }
            }
        }
        activityBid.setJoinState(joinState);
        activityBid.setItems(items);
        return activityBid;
    }

    private Auction assembleAuction(Auction auction) {
        int joinState = AuctionJoinState.VIABLE.getCode();
        if (StringUtils.equals(auction.getApplierId(), UserContext.getUserId())) {//自己
            joinState = AuctionJoinState.ONESELF.getCode();
        } else if (AuctionStatus.FINISH.getCode().equals(auction.getStatus())) {//已结束
            joinState = AuctionJoinState.FINISH.getCode();
            if (StringUtils.equals(auction.getBidderId(),UserContext.getUserId())){//竞拍成功
                joinState = AuctionJoinState.SUCCESS.getCode();
                if (AuctionStatus.EVALUATE.getCode().equals(auction.getStatus())){//已评价
                    joinState = AuctionJoinState.EVALUATE.getCode();
                }
            }
        }
        auction.setJoinState(joinState);
        auction.setUser(userService.getUserSimpleById(auction.getApplierId()));
        return auction;
    }
}
