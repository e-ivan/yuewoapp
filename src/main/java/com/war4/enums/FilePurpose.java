package com.war4.enums;

import com.war4.pojo.*;
import com.war4.vo.film.CinemaVO;
import com.war4.vo.film.MovieVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dly on 2016/8/5.
 */
@Getter
@AllArgsConstructor
public enum FilePurpose {

    IMAGE_HOME_PAGE("imageHomePageAd",HomePage.class,500D),//首页广告

    IMAGE_HOME_PAGE_SHARE("imageHomePageShare",HomePage.class,null),//分享图片

    IMAGE_HOME_PAGE_POP("imageHomePagePopUp",HomePage.class,null),//弹出框图片

    IMAGE_KOU_MOVIE("imageKouMovie",MovieVO.class,null),//

    IMAGE_MOVIE("imageMovie",MovieVO.class,200D),//电影图片

    IMAGE_CINEMA("imageCinema",CinemaVO.class,null),//

    IMAGE_ACTIVITY("imageActivity",Activity.class,null),//活动图片

    IMAGE_ALBUM("imageAlbum",UserInfoBase.class,240D),//相册图片

    IMAGE_ANCHOR_PHOTO("imageAnchorPhoto",UserInfoBase.class,null),//直播图片

    IMAGE_GIFT("imageGift",Gift.class,70D),//礼物图片

    ACTOR_RESUME_COVER_PHOTO("actorResumeCoverPhoto",ActorResume.class,null),//演员简历封面

    ACTOR_RESUME_PHOTO_WALL("actorResumePhotoWall",ActorResume.class,null),//演员简历照片墙

    DIRECTOR_RESUME_COVER_PHOTO("directorResumeCoverPhoto",DirectorResume.class,null),//导演简历封面

    DIRECTOR_RESUME_PHOTO_WALL("directorResumePhotoWall",DirectorResume.class,null),//导演简历照片墙

    SECEENWRITER_RESUME_COVER_PHOTO("seceenwriterResumeCoverPhoto",SeceenwriterResume.class,null),//编剧简历封面

    SECEENWRITER_RESUME_PHOTO_WALL("seceenwriterResumePhotoWall",SeceenwriterResume.class,null),//编剧简历照片墙

    RESUME_ITEM_PHOTO("resumeItemPhoto",ResumeWorItem.class,null),//作品图片

    CREW_PHOTO("crewPhoto",Crew.class,null),//剧组图片

    PROJECT_PHOTO("projectPhoto",Project.class,null),//项目图片

    PROJECT_DETAIL_PHOTO("projectDeatilPhoto",Project.class,null), //项目详情图片

    FILM_PHOTO("filmPhoto",Film.class,null),//电影图片

    FILM_VIDEO("filmVideo",Film.class,null),//电影文件

    FILM_TOPIC_PHOTO("filmTopicPhoto",FilmTopic.class,null),//电影话题图片

    TRAILER_PHOTO("trailerPhoto",Trailer.class,null),//直播预告

    REAL_NAME("realNamePicture",UserRealName.class,100D),//实名认证图片

    IMAGE_USER_PHOTO_HEAD("imageUserPhotoHead",UserInfoBase.class,120D),//用户头像

    MOMENT_PHOTO("imageMomentPhoto",Moment.class,250D),//朋友圈相册

    MOMENT_BACKGROUND("imageMomentBackground",UserInfoBase.class,800D),//朋友圈背景图

    USER_ID_CARD_PHOTO("imageUserIdCard",UserInfoBase.class,null),//会员身份证图

    USER_ENROLL_FORM_ALBUM("imageUserEnrollFormAlbum",UserInfoBase.class,null),//会员报名表图

    AUCTION_ALBUM("auctionAlbum",Auction.class,500D),//竞拍内容相册

    AD_PAGE_IMAGE("adPageImage",AdPage.class,300D),//宣传页面

    USER_VIDEO("userVideo",UserInfoBase.class,null),//个人视频

    APP_APK("apk",VersionUpgrade.class,null),
    ;


    private String code;
    private Class belongToClass;
    private Double smallHeight;//压缩高度


    public static FilePurpose getByCode(String code){
        for(FilePurpose filePurpose: FilePurpose.values()){
            if (filePurpose.getCode().equals(code)){
                return filePurpose;
            }
        }
        return null;
    }
}
