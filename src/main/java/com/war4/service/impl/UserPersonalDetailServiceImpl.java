package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.FilmCollectionService;
import com.war4.service.NearbyDatingService;
import com.war4.service.SystemDictionaryService;
import com.war4.service.UserPersonalDetailService;
import com.war4.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hh on 2017.8.30 0030.
 */
@Service
public class UserPersonalDetailServiceImpl implements UserPersonalDetailService {
    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private FilmCollectionService filmCollectionService;
    @Autowired
    private SystemDictionaryService systemDictionaryService;
    @Autowired
    private NearbyDatingService nearbyDatingService;

    @Override
    @Transactional
    public void createUserPersonalDetail(String userId) {
        UserPersonalDetail upd = new UserPersonalDetail();
        upd.setUserId(userId);
        upd.setCreated(new Date());
        upd.setUpdated(new Date());
        baseRepository.saveObj(upd);
    }

    @Override
    @Transactional
    public UserPersonalDetail updateUserPersonalDetail(UserPersonalDetail userPersonalDetail) {
        UserPersonalDetail upd = baseRepository.getObjById(UserPersonalDetail.class, userPersonalDetail.getUserId());
        if (upd == null){
            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"用户不存在！");
        }
        upd.setBook(userPersonalDetail.getBook());
        upd.setCompany(userPersonalDetail.getCompany());
        upd.setFood(userPersonalDetail.getFood());
        upd.setHometown(userPersonalDetail.getHometown());
        upd.setJob(userPersonalDetail.getJob());
        upd.setJobField(userPersonalDetail.getJobField());
        upd.setMusic(userPersonalDetail.getMusic());
        upd.setSport(userPersonalDetail.getSport());
        upd.setTag(userPersonalDetail.getTag());
        upd.setTravel(userPersonalDetail.getTravel());
        upd.setIncome(userPersonalDetail.getIncome());
        upd.setEducation(userPersonalDetail.getEducation());
        upd.setDateImpression(userPersonalDetail.getDateImpression());
        upd.setUpdated(new Date());
        baseRepository.updateObj(upd);
        return upd;
    }

    @Override
    public UserPersonalDetail getUserPersonalDetail(String userId) throws Exception {
        UserPersonalDetail userPersonalDetail = baseRepository.getObjById(UserPersonalDetail.class,userId);
        if (userPersonalDetail != null) {
            CutPage<FilmCollection> filmCollectionCutPage = filmCollectionService.queryMyCollection(userId, null, null);
            userPersonalDetail.setFilmCollectionList(filmCollectionCutPage.getiData());
            CutPage<NearbyDating> cutPage = nearbyDatingService.queryUserNearbyDating(userId, false, 0, 2);
            List<Map<String, Object>> nearbyDatingList = cutPage.getiData().stream().map(n -> {
                Map<String, Object> map = new HashMap<>();
                map.put("nId", n.getId());
                map.put("title",nearbyDatingTitleFormat(n.getMovieName()));
                return map;
            }).collect(Collectors.toList());
            userPersonalDetail.setNearbyDatingList(nearbyDatingList);
        }
        return userPersonalDetail;
    }

    private static String nearbyDatingTitleFormat(String movieName){
        if (StringUtils.isBlank(movieName)) {
            movieName = "任意影片";
        }
        return MessageFormat.format("看电影·{0}", movieName);
    }

    @Override
    @Transactional
    public void setUserDateImpression(String userId) {
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userId);
        if (user != null){
            String sex = "女生";
            String call = "她";
            String sn1 = "impressionItem01";
            String sn2 = "impressionItem02";
            String impressionTemp = systemDictionaryService.getSystemDictionaryBySN("dateImpression",true).getItems().get(0).getTitle();
            if (user.getSex() == UserInfoBase.MALE){
                sex = "男生";
                call = "他";
                sn1 = "impressionItem11";
                sn2 = "impressionItem12";
            }
            List<SystemDictionaryItem> impressionItem1 = systemDictionaryService.getSystemDictionaryBySN(sn1,true).getItems();
            Integer random1 = Integer.parseInt(NumberUtil.getAroundVal(String.valueOf(impressionItem1.size()), "0", 0));
            List<SystemDictionaryItem> impressionItem2 = systemDictionaryService.getSystemDictionaryBySN(sn2,true).getItems();
            Integer random2 = Integer.parseInt(NumberUtil.getAroundVal(String.valueOf(impressionItem2.size()), "0", 0));
            int index1 = random1 - 1 < 0 ? 0 : random1 - 1;
            int index2 = random2 - 1 < 0 ? 0 : random2 - 1;
            String dateImpression = MessageFormat.format(impressionTemp,"",sex, call,
                    impressionItem1.get(index1).getTitle(), impressionItem2.get(index2).getTitle());
            UserPersonalDetail userPersonalDetail = baseRepository.getObjById(UserPersonalDetail.class,userId);
            userPersonalDetail.setDateImpression(dateImpression);
            baseRepository.updateObj(userPersonalDetail);
        }
    }


}
