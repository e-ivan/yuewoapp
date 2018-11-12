package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.enums.*;
import com.war4.listener.event.DatingEvent;
import com.war4.listener.event.PushEvent;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.util.DateUtil;
import com.war4.util.OrderUtil;
import com.war4.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2016/12/19.
 */
@Service
public class DatingFilmServiceImpl implements DatingFilmService, ApplicationContextAware {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;


    @Autowired
    private UserGiftService userGiftService;


    @Autowired
    private FileService fileService;

    @Autowired
    UserCouponService userCouponService;

    @Autowired
    UserOtherCouponService userOtherCouponService;

    @Autowired
    CouponService couponService;

    private ApplicationContext ac;


    @Override
    @Transactional
    public void addDatingFilm(DatingFilm datingFilm) {
        String createUserId = datingFilm.getCreateUserId();
        if (StringUtils.isBlank(datingFilm.getCreateUserId())) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "createUserId不能为空！");
        }
        String acceptUserId = datingFilm.getAcceptUserId();
        if (StringUtils.isBlank(datingFilm.getAcceptUserId())) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "acceptUserId不能为空！");
        }
        if (datingFilm.getCreateUserId().equals(datingFilm.getAcceptUserId())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不能约自己看电影哟！");
        }

        DatingFilm df = new DatingFilm();
        df.setId(UUID.randomUUID().toString());
        df.setCreateUserId(createUserId);
        df.setAcceptUserId(acceptUserId);
        df.setTitle(datingFilm.getTitle());
        df.setIntro(datingFilm.getIntro());
        df.setDatingType(datingFilm.getDatingType());
        df.setDatingFilm(datingFilm.getDatingFilm());
        df.setPayType(datingFilm.getPayType());
        df.setDatingCinema(datingFilm.getDatingCinema());
        df.setDatingTime(datingFilm.getDatingTime());
        df.setTransport(datingFilm.getTransport());
        df.setStatus(DatingStatus.CREATE.getCode());
        df.setCanBringFriends(datingFilm.getCanBringFriends());
        df.setFilmId(datingFilm.getFilmId());
        baseRepository.saveObj(df);
        UserInfoBase userInfoBase = baseRepository.getObjById(UserInfoBase.class, createUserId);
        //推送消息
        PushMsgVO pmv = new PushMsgVO(acceptUserId, createUserId, userInfoBase.getNickName() + " 约你看电影",
                userInfoBase.getNickName() + "约 ID " + acceptUserId + " 看电影",
                datingFilm.getId(), MessageLogType.DATING_FILM, true);
        pmv.addExtras("filmId", datingFilm.getFilmId());
        ac.publishEvent(new PushEvent(pmv));
    }

    @Override
    public DatingFilm queryDatingFilmForId(String datingId) {
        if (datingId == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "datingId不能为空！");
        }
        DatingFilm datingFilm = baseRepository.getObjById(DatingFilm.class, datingId);
        if (datingFilm == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该约会！");
        }
        datingFilm = assembleService.assembleObject(datingFilm);
        datingFilm.setUserGift(userGiftService.queryUserGiftById(datingFilm.getFkUserGiftId()));

        //zzc
        //判断约会是否在优惠券期间
        if (DateUtil.isBetweenPeriod(PropertiesConfig.getDatingGetFilmsStartDate(), PropertiesConfig.getDatingGetFilmsEndDate(), datingFilm.getCreateTime())) {
            datingFilm.setIsActivityTime(1);//活动期间
        } else {
            datingFilm.setIsActivityTime(0);//不在活动期间
        }
        return datingFilm;
    }


    @Override
    public CutPage<DatingFilm> queryMyDating(String userId, Integer type, Integer page, Integer limit) {
        if (userId == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "userId不能为空！");
        }
        String hql = baseRepository.getBaseHqlByClass(DatingFilm.class);
        if (type == null) {
            hql += " and (createUserId='" + userId + "' or acceptUserId = '" + userId + "' )";
        } else if (type.equals(DatingType.CREATE.getCode())) {
            hql += " and createUserId ='" + userId + "'";
        } else if (type.equals(DatingType.ACCPET.getCode())) {
            hql += " and acceptUserId ='" + userId + "'";
        }

        hql += " order by status,createTime desc ";
        CutPage<DatingFilm> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql, cutPage);
        for (DatingFilm datingFilm : cutPage.getiData()) {
            assembleService.assembleObject(datingFilm);
        }
        return cutPage;
    }


    @Override
    @Transactional
    public DatingFilm updateDatingSettingAccept(String datingId) {
        if (datingId == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "datingId不能为空！");
        }
        DatingFilm datingFilm = baseRepository.getObjById(DatingFilm.class, datingId);
        if (datingFilm == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该约会！");
        }
        datingFilm.setStatus(DatingStatus.SUCCESSFUL.getCode());
        baseRepository.updateObj(datingFilm);
        UserInfoBase acceptUser = baseRepository.getObjById(UserInfoBase.class, datingFilm.getAcceptUserId());

        ///
        //推送消息
        PushMsgVO pmv = new PushMsgVO(datingFilm.getCreateUserId(), datingFilm.getAcceptUserId(),
                acceptUser.getNickName() + " 同意了你的约会邀请",
                acceptUser.getNickName() + "同意 ID " + datingFilm.getCreateUserId() + " 邀请看电影",
                datingFilm.getId(), MessageLogType.DATING_FILM, true);
        pmv.addExtras("filmId", datingFilm.getFilmId());
        pmv.addExtras("state", "success");


        //找到符合资格的优惠券id
        String couponIdsStr2 = PropertiesConfig.getDatingGetFilmsCouponId();
        String[] couponIds = couponIdsStr2.split(",");
        List<Integer> couponMoneys = couponService.queryCouponMoneys(couponIds);
        //判断是否在赠送优惠券活动期间
        if (DateUtil.isBetweenPeriod(PropertiesConfig.getDatingGetFilmsStartDate(), PropertiesConfig.getDatingGetFilmsEndDate(), datingFilm.getCreateTime())) {


            if (Objects.equals(datingFilm.getPayType(), DatingPayType.SENDER.getCode())) {
                //对自己处理
                datingFilm.setCouPonDenomination(0);

                //对对方处理
                String createUserId = datingFilm.getCreateUserId(); //得到自己id

                boolean canGetCoupon = this.canGetCoupon(createUserId, couponIds);

                //是否有资格获取优惠券
                if (canGetCoupon) {
                    pmv.addExtras("couPonDenomination", Integer.toString(couponMoneys.get(1)));
                } else {
                    pmv.addExtras("couPonDenomination", "0");

                }

            } else if (datingFilm.getPayType() == 1) {

                //对对方处理
                pmv.addExtras("couPonDenomination", "0");

                //对自己处理
                String acceptUserId = datingFilm.getAcceptUserId(); //得到自己id

                boolean canGetCoupon = this.canGetCoupon(acceptUserId, couponIds);

                //是否有资格获取优惠券
                if (canGetCoupon) {
                    datingFilm.setCouPonDenomination(couponMoneys.get(1));
                } else {
                    datingFilm.setCouPonDenomination(0);
                }

            } else if (datingFilm.getPayType() == 2) {

                //对自己处理
                String acceptUserId = datingFilm.getAcceptUserId(); //得到自己id

                boolean canGetCouponAccept = this.canGetCoupon(acceptUserId, couponIds);

                //是否有资格获取优惠券
                if (canGetCouponAccept) {
                    datingFilm.setCouPonDenomination(couponMoneys.get(0));
                } else {
                    datingFilm.setCouPonDenomination(0);
                }


                //对对方处理
                String createUserId = datingFilm.getCreateUserId(); //得到自己id

                boolean canGetCouponCreate = this.canGetCoupon(createUserId, couponIds);

                //是否有资格获取优惠券
                if (canGetCouponCreate) {
                    pmv.addExtras("couPonDenomination", Integer.toString(couponMoneys.get(0)));
                } else {
                    pmv.addExtras("couPonDenomination", "0");

                }

            }

        } else { //不在优惠期
            datingFilm.setCouPonDenomination(0);
            pmv.addExtras("couPonDenomination", "0");
        }

        pmv.addExtras("payType", datingFilm.getPayType().toString());
        pmv.addExtras("datingId", datingFilm.getId());
        ac.publishEvent(new PushEvent(pmv));
        //推送约会成功事件
        ac.publishEvent(new DatingEvent(datingFilm));

        return datingFilm;
    }


    private boolean canGetCoupon(String userId, String[] couponIds) {

        CutPage<UserOtherCoupon> userCouponCutPage = userOtherCouponService.queryAllDatingUserOtherCouponByUserId(userId, couponIds);

        if (userCouponCutPage.getiData().size() != 0) {

            UserOtherCoupon userCoupon = userCouponCutPage.getiData().get(0);

            Date createTime = userCoupon.getCreateTime();//得到创建时间

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            String creatStr = formatter.format(createTime);

            try {

                Date dateTemp_createTime = formatter.parse(creatStr);
                //判断是否今天赠送过
                if (DateUtil.isNow(dateTemp_createTime, "yyyy-MM-dd")) {
                    return false;

                } else {
                    return true;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void updateDatingSettingRefuse(String datingId) {
        if (datingId == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "datingId不能为空！");
        }
        DatingFilm datingFilm = baseRepository.getObjById(DatingFilm.class, datingId);
        if (datingFilm == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该约会！");
        }
        datingFilm.setStatus(DatingStatus.CLOSE.getCode());

        baseRepository.updateObj(datingFilm);
    }

    @Override
    @Transactional
    public void deleteDatingSetting(String datingId) {
        if (datingId == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "datingId不能为空！");
        }
        DatingFilm datingFilm = baseRepository.getObjById(DatingFilm.class, datingId);
        if (datingFilm == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "找不到该约会！");
        }
        baseRepository.logicDelete(datingFilm);
    }

    @Override
    public CutPage<DatingUserVO> queryDatingUser(QueryDatingUserVO vo) {
        if (StringUtils.isBlank(vo.getLng()) || StringUtils.isBlank(vo.getLat())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "无法定位当前位置");
        }
        StringBuilder hql = new StringBuilder(500);
        Map<String, Object> params = new HashMap<>();
        hql.append("SELECT * FROM dating_setting d RIGHT JOIN (")
                .append("SELECT id,username,phone,nick_name,sex,intro,interest,job,constellation,birth,user_photo_head,internal_flag,create_time,update_time,")
                .append("ST_DISTANCE(point,point(:lng,:lat)) * 111195 distance,TIMESTAMPDIFF(YEAR,birth,CURDATE()) age FROM user_info_base WHERE")
                .append(" user_photo_head IS NOT NULL AND point IS NOT NULL AND head_state != 0 ");
        if (StringUtils.isNotBlank(vo.getUserId())) {
            hql.append(" AND id != :userId ");
            params.put("userId", vo.getUserId());
        }
        if (vo.getSex() != null && vo.getSex() > -1) {
            hql.append(" AND sex = :sex");
            params.put("sex", vo.getSex());
        }
        hql.append(") u ON(d.fk_user_id = u.id) WHERE d.is_accept = :accept ");
        if (vo.getStartAge() != null) {
            hql.append(" AND u.age >= :startAge");
            params.put("startAge", vo.getStartAge());
        }
        if (vo.getEndAge() != null) {
            hql.append(" AND u.age <= :endAge");
            params.put("endAge", vo.getEndAge());
        }
        if (vo.getMinDist() != null) {
            hql.append(" AND u.distance >= :minDist");
            params.put("minDist", vo.getMinDist());
        }
        if (vo.getMaxDist() != null) {
            hql.append(" AND u.distance <= :maxDist");
            params.put("maxDist", vo.getMaxDist());
        }
        params.put("lng", vo.getLng());
        params.put("lat", vo.getLat());
        params.put("accept", DatingSettingIsAccept.ACCEPT.getCode());
        hql.append(" ORDER BY u.create_time DESC,u.distance,u.update_time DESC");
        CutPage<DatingUserVO> datingUsers = baseRepository.executeSql(hql.toString(), params, DatingUserVO.class, vo.getPage(), vo.getLimit());
        for (DatingUserVO datingUserVO : datingUsers.getiData()) {
            List<FileUpload> albumList = fileService.getFilesByConditions(FileTypeAndPath.IMAGE_TYPE_PATH.getType(),
                    FilePurpose.IMAGE_ALBUM.getCode(),
                    datingUserVO.getId(),
                    CutPage.MAX_COUNT).getiData();
            if (albumList != null && albumList.size() > 0) {
                datingUserVO.setAlbumList(albumList);
            }
        }

        return datingUsers;
    }

    @Override
    public CutPage<DatingUserLatestVO> queryDatingUserLatest(QueryDatingUserVO vo) {
        Map<String, Object> params = new HashMap<>();
        return baseRepository.executeSql(crateSqlAndParams(vo, params), params, DatingUserLatestVO.class, vo.getPage(), vo.getLimit());
    }

    @Override
    public long queryDatingUserCount(QueryDatingUserVO vo) {
        if (StringUtils.isBlank(vo.getLng()) || StringUtils.isBlank(vo.getLat())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "无法定位当前位置");
        }
        Map<String, Object> params = new HashMap<>();
        vo.setInternal(null);
        return baseRepository.executeSql(crateSqlAndParams(vo, params), params, null);
    }

    private StringBuilder crateSqlAndParams(QueryDatingUserVO vo, Map<String, Object> params) {
        StringBuilder sql = new StringBuilder(500);
        boolean flag = vo.getInternal() != null;
        sql.append("SELECT ").append(flag ? " * " : "COUNT(u.id) ")
                .append("FROM dating_setting d RIGHT JOIN (")
                .append("SELECT id,nick_name,sex,constellation,user_photo_head,create_time,update_time,internal_flag,")
                .append("ST_DISTANCE(point,point(:lng,:lat)) * 111195 distance,TIMESTAMPDIFF(YEAR,birth,CURDATE()) age FROM user_info_base WHERE")
                .append(" user_photo_head IS NOT NULL AND point IS NOT NULL AND head_state = :headState ");
        if (flag) {
            sql.append("AND internal_flag = :internal");
            params.put("internal", vo.getInternal());
        }
        if (StringUtils.isNotBlank(vo.getUserId())) {
            sql.append(" AND id != :userId ");
            params.put("userId", vo.getUserId());
        }
        if (vo.getSex() != null && vo.getSex() > -1) {
            sql.append(" AND sex = :sex");
            params.put("sex", vo.getSex());
        }
        sql.append(") u ON(d.fk_user_id = u.id) WHERE d.is_accept = :accept ");
        if (vo.getStartAge() != null) {
            sql.append(" AND u.age >= :startAge");
            params.put("startAge", vo.getStartAge());
        }
        if (vo.getEndAge() != null) {
            sql.append(" AND u.age <= :endAge");
            params.put("endAge", vo.getEndAge());
        }
        if (flag && vo.getInternal() != 1) {
            if (vo.getMinDist() != null) {
                sql.append(" AND u.distance >= :minDist");
                params.put("minDist", vo.getMinDist());
            }
            if (vo.getMaxDist() != null) {
                sql.append(" AND u.distance <= :maxDist");
                params.put("maxDist", vo.getMaxDist());
            }
            sql.append(" ORDER BY u.distance,u.update_time DESC");
        } else {
            sql.append(" ORDER BY u.create_time DESC,u.update_time DESC");
        }
        params.put("lng", vo.getLng());
        params.put("lat", vo.getLat());
        params.put("headState", UserHeadStatus.PASS.getCode());
        params.put("accept", DatingSettingIsAccept.ACCEPT.getCode());
        return sql;
    }

    public List<DatingUserLatestVO> queryUserImageAlbumCount(List<DatingUserLatestVO> users) {
        if (users.size() <= 0) {
            return users;
        }
        String hql = "select new com.war4.vo.UserImageAlbumVO(fkPurposeId,count(*)) from FileUpload where delFlag = 0 and purpose = :purpose group by fkPurposeId having fkPurposeId in(:userIds) order by field(fkPurposeId,:userIds) ";
        Map<String, Object> paramMap = baseRepository.paramMap();
        List<String> ids = users.stream().map(DatingUserLatestVO::getId).collect(Collectors.toList());
        paramMap.put("purpose", FilePurpose.IMAGE_ALBUM.getCode());
        paramMap.put("userIds", ids);
        List<UserImageAlbumVO> result = baseRepository.queryHqlResult(hql, paramMap, 0, ids.size());
        Map<String, Long> map = result.stream().collect(Collectors.toMap(UserImageAlbumVO::getFkPurposeId, UserImageAlbumVO::getCount));
        users.forEach(vo -> vo.setAlbumSize((map.get(vo.getId()) == null ? 0 : map.get(vo.getId()).intValue()) + 1));
        return users;
    }

    @Override
    @Transactional
    public void evaluateDating(String userId, String appraiser, Float fenShu, String content) {
        UserEvaluate userEvaluate = new UserEvaluate();
        userEvaluate.setId(OrderUtil.getUUID());
        userEvaluate.setContent(content);
        userEvaluate.setUserId(userId);
        userEvaluate.setFenShu(fenShu);
        userEvaluate.setAppraiser(appraiser);
        baseRepository.saveObj(userEvaluate);
    }

    @Override
    public CutPage<UserEvaluate> queryUserEvaluate(String userId, Integer page, Integer limit) {
        String hql = baseRepository.getBaseHqlByClass(UserEvaluate.class);
        hql += "and appraiser= '" + userId + "'";
        CutPage<UserEvaluate> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql, cutPage);
        for (UserEvaluate userEvaluate : cutPage.getiData()) {
            UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, userEvaluate.getUserId());
            assembleService.assembleObject(user);
            userEvaluate.setUserInfoBase(user);
            UserInfoBase appraiserUser = baseRepository.getObjById(UserInfoBase.class, userEvaluate.getAppraiser());
            assembleService.assembleObject(appraiserUser);
            userEvaluate.setAppraiserInfoBase(appraiserUser);
        }
        return cutPage;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    @Override
    public CutPage<DatingFilm> queryUserAllDatingFilm(String userId) {
        String hql = "from DatingFilm where (createUserId = '" + userId + "' or acceptUserId = '" + userId + "') and delFlag = 0 and status = 30 order by createTime desc";

        CutPage<DatingFilm> cutPage = baseRepository.executeHql(hql, null, 0, Integer.MAX_VALUE);

        return cutPage;
    }

    @Override
    public CutPage<DatingFilm> queryAllDating(String status, String keyword, Integer page, Integer limit) {

        StringBuilder hql = new StringBuilder(200);
        Map<String, Object> map = new HashMap<>();
        hql.append("from DatingFilm where 1=1");

        if (StringUtils.isNotBlank(keyword)) {
            hql.append(" and (createUserId like :keyword or acceptUserId like :keyword or datingFilm like :keyword) ");
            map.put("keyword", "%" + keyword + "%");
        }

        if (StringUtils.isNotBlank(status)) {
            hql.append(" and (status = :status) ");
            map.put("status", Integer.parseInt(status));
        }

        hql.append(" order by createTime desc");
        CutPage<DatingFilm> datingFilmCutPage = baseRepository.executeHql(hql.toString(), map, page, limit);
        return datingFilmCutPage;

    }

}
