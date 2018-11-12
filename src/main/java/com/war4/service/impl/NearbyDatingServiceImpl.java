package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.NearbyDatingState;
import com.war4.pojo.NearbyDating;
import com.war4.pojo.NearbyDatingApplyItem;
import com.war4.pojo.UserInfoBase;
import com.war4.service.KouDianYingService;
import com.war4.service.NearbyDatingService;
import com.war4.service.UserInfoBaseService;
import com.war4.util.MapDistance;
import com.war4.util.UserContext;
import com.war4.vo.NearbyDatingParamVO;
import com.war4.vo.SimpleUserVO;
import com.war4.vo.film.CinemaVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by E_Iva on 2018.3.23.0023.
 */
@Service
public class NearbyDatingServiceImpl extends BaseService implements NearbyDatingService {
    @Autowired
    private KouDianYingService kouDianYingService;
    @Autowired
    private UserInfoBaseService userInfoBaseService;

    @Override
    @Transactional
    public void createNearbyDating(NearbyDatingParamVO vo) {
        if (vo.getDatingDate() == null || vo.getDatingDate().before(new Date()))
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "约会时间不能小于当前时间");
        if (vo.getSex() == null || vo.getPaySite() == null)
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "参数缺失");
        //查询用户最近是否有在线的约会
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, UserContext.getUserId());
        if (user.getBirth() == null || Objects.equals(user.getSex(),-1) || StringUtils.isBlank(user.getUserPhotoHead())){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "请完善个人信息");
        }
        NearbyDating latestNearbyDating = this.queryUserLatestNearbyDating(user.getId());
        if (latestNearbyDating != null && latestNearbyDating.getDatingDate().after(new Date()))
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "您最近已有一个约会在进行！");
        NearbyDating nearbyDating = new NearbyDating();
        if (StringUtils.isNoneBlank(vo.getMovieName(), vo.getPic())) {
            nearbyDating.setPic(vo.getPic());
            nearbyDating.setMovieName(vo.getMovieName());
        }
        nearbyDating.setUserId(user.getId());
        nearbyDating.setContent(vo.getContent());
        nearbyDating.setDatingDate(vo.getDatingDate());
        nearbyDating.setSex(vo.getSex());
        nearbyDating.setPaySite(vo.getPaySite());
        //查询影院
        CinemaVO cinema = kouDianYingService.getCinemaById(vo.getCinemaId());
        if (cinema != null) {
            nearbyDating.setSite(cinema.getCityName() + "-" + cinema.getDistrictName() + "," + cinema.getCinemaName());
            nearbyDating.setLng(cinema.getLongitude());
            nearbyDating.setLat(cinema.getLatitude());
        } else {
            nearbyDating.setLat(user.getY());
            nearbyDating.setLng(user.getX());
        }
        nearbyDating.setState(NearbyDatingState.NORMAL.getCode());
        baseRepository.saveObj(nearbyDating);
    }

    @Override
    @Transactional
    public void createNearbyDatingApplyItem(Long nId) {
        NearbyDating nearbyDating = baseRepository.getObjById(NearbyDating.class, nId);
        if (nearbyDating == null || !NearbyDatingState.NORMAL.getCode().equals(nearbyDating.getState())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "约会不存在");
        }
        if (new Date().after(nearbyDating.getDatingDate())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "约会已过期");
        }
        UserInfoBase user = baseRepository.getObjById(UserInfoBase.class, UserContext.getUserId());
        if (!nearbyDating.getSex().equals(-1) && !nearbyDating.getSex().equals(user.getSex())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "不符合约会条件");
        }
        NearbyDatingApplyItem item = new NearbyDatingApplyItem();
        item.setUserId(UserContext.getUserId());
        item.setNId(nearbyDating.getId());
        baseRepository.saveObj(item);
        nearbyDating.setApplyCount(nearbyDating.getApplyCount() + 1);
        baseRepository.updateObj(nearbyDating);
    }

    @Override
    public void cancelNearbyDating(Long nId, String createUser) {
        NearbyDating nearbyDating = baseRepository.getObjById(NearbyDating.class, nId);
        if (nearbyDating != null && StringUtils.equals(nearbyDating.getUserId(),createUser)) {
            nearbyDating.setState(NearbyDatingState.CANCEL.getCode());
            baseRepository.updateObj(nearbyDating);
        }
    }

    @Override
    public NearbyDating getNearbyDatingById(Long nId, String lng, String lat) {
        NearbyDating nearbyDating = baseRepository.getObjById(NearbyDating.class, nId);
        if (nearbyDating != null) {
            List<NearbyDatingApplyItem> is = this.queryApplyItem(nearbyDating.getId(), UserContext.getUserId());
            if (is.size() > 0) {
                nearbyDating.setJoinState(true);
            }
            //获取发起人的信息
            Map<String, SimpleUserVO> map = userInfoBaseService.querySimpleUserMap(Collections.singletonList(nearbyDating.getUserId()), lng, lat);
            nearbyDating.setUser(map.get(nearbyDating.getUserId()));
            List<NearbyDatingApplyItem> items = this.queryApplyItem(nearbyDating.getId(), null);
            //获取用户id
            List<String> userIds = items.stream().map(NearbyDatingApplyItem::getUserId).collect(Collectors.toList());
            Map<String, UserInfoBase> userMap = userInfoBaseService.queryUserMap(userIds);
            items.forEach(i -> {
                UserInfoBase u = userMap.get(i.getUserId());
                if (u != null) {
                    i.setUser(new SimpleUserVO(i.getUserId(), u.getUserPhotoHead(), u.getAge(), u.getSex(), u.getNickName(),
                            MapDistance.getDistance(u.getX(), u.getY(), lng, lat)));
                }
            });
            nearbyDating.setApplyItems(items);
            nearbyDating.setViewCount(nearbyDating.getViewCount() + 1);
            baseRepository.updateObj(nearbyDating);
        }
        return nearbyDating;
    }

    @Override
    public CutPage<NearbyDating> queryNearbyDatingByDistance(String lng, String lat, Integer page, Integer limit) {
        StringBuilder sql = new StringBuilder(200);
        sql.append("SELECT n.*,")
                .append("ST_DISTANCE(u.point,point(:lng,:lat)) * 111195 distance FROM")
                .append(" nearby_dating n LEFT JOIN user_info_base u ON(n.user_id = u.id)")
                .append(" WHERE n.state = :state AND n.dating_date >= :now ");
        sql.append("ORDER BY distance ");
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("lng",lng);
        paramMap.put("lat", lat);
        paramMap.put("state",NearbyDatingState.NORMAL.getCode());
        paramMap.put("now",new Date());
        CutPage<NearbyDating> cutPage = baseRepository.executeSql(sql, paramMap, NearbyDating.class, page, limit);
        List<NearbyDating> datings = cutPage.getiData();
        List<String> userIds = datings.stream().map(NearbyDating::getUserId).collect(Collectors.toList());
        Map<String, SimpleUserVO> userMap = userInfoBaseService.querySimpleUserMap(userIds, lng, lat);
        datings.forEach(d -> d.setUser(userMap.get(d.getUserId())));
        return cutPage;
    }

    @Override
    public CutPage<NearbyDating> queryAllNearbyDating(String keyword, Integer state, Integer page, Integer limit) {
        StringBuilder sql = new StringBuilder(100);
        Map<String, Object> map = baseRepository.paramMap();
        sql.append("SELECT n.* FROM nearby_dating n LEFT JOIN user_info_base u ON (n.user_id = u.id) WHERE (n.state = :state ");
        switch (state) {
            case 0 :
                map.put("state",NearbyDatingState.CANCEL.getCode());
                sql.append(" OR n.dating_date < :now)");
                break;
            case 1 :
                sql.append(" AND n.dating_date >= :now)");
                map.put("state",NearbyDatingState.NORMAL.getCode());
                break;
        }
        map.put("now",new Date());
        if (StringUtils.isNoneBlank(keyword)) {
            sql.append(" AND (n.content like :keyword OR u.nick_name like :keyword OR u.id like :keyword) ");
            map.put("keyword","%" + keyword + "%");
        }
        sql.append(" ORDER BY created DESC ");
        CutPage<NearbyDating> cutPage = baseRepository.executeSql(sql, map,NearbyDating.class, page, limit);
        List<NearbyDating> datings = cutPage.getiData();
        List<String> userIds = datings.stream().map(NearbyDating::getUserId).collect(Collectors.toList());
        Map<String, SimpleUserVO> userMap = userInfoBaseService.querySimpleUserMap(userIds);
        datings.forEach(d -> d.setUser(userMap.get(d.getUserId())));
        return cutPage;
    }

    @Override
    public CutPage<NearbyDatingApplyItem> queryNearbyDatingApplyItem(Long nId, Integer page, Integer limit) {
        String hql = "from NearbyDatingApplyItem where nId = :nId order by created";
        Map<String, Object> map = baseRepository.paramMap();
        map.put("nId",nId);
        CutPage<NearbyDatingApplyItem> cutPage = baseRepository.executeHql(hql, map, page, limit);
        List<NearbyDatingApplyItem> items = cutPage.getiData();
        List<String> userIds = items.stream().map(NearbyDatingApplyItem::getUserId).collect(Collectors.toList());
        Map<String, SimpleUserVO> userMap = userInfoBaseService.querySimpleUserMap(userIds);
        items.forEach(d -> d.setUser(userMap.get(d.getUserId())));
        return cutPage;
    }

    @Override
    public CutPage<NearbyDating> queryUserNearbyDating(String userId, boolean setUser,Integer page, Integer limit) {
        String hql = "from NearbyDating where userId = :userId and state = :state order by created desc";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("userId", userId);
        paramMap.put("state", NearbyDatingState.NORMAL.getCode());
        CutPage<NearbyDating> cutPage = baseRepository.executeHql(hql, paramMap, page, limit);
        //获取发起人的信息
        if (setUser) {
            Map<String, SimpleUserVO> map = userInfoBaseService.querySimpleUserMap(Collections.singletonList(userId));
            cutPage.getiData().forEach(n -> n.setUser(map.get(userId)));
        }
        return cutPage;
    }

    @Override
    public CutPage<NearbyDating> queryUserApplyNearbyDating(String userId, Integer page, Integer limit) {
        String hql = "SELECT n.* FROM nearby_dating_apply_item i LEFT JOIN nearby_dating n ON i.n_id = n.id WHERE i.user_id = :userId ORDER BY i.created DESC";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("userId", userId);
        CutPage<NearbyDating> cutPage = baseRepository.executeSql(hql, paramMap,NearbyDating.class, page, limit);
        List<NearbyDating> datings = cutPage.getiData();
        List<String> userIds = datings.stream().map(NearbyDating::getUserId).collect(Collectors.toList());
        Map<String, SimpleUserVO> map = userInfoBaseService.querySimpleUserMap(userIds);
        datings.forEach(n -> n.setUser(map.get(n.getUserId())));
        return cutPage;
    }

    /**
     * 查询用户最新发布的附近有约
     *
     * @param userId
     * @return
     */
    private NearbyDating queryUserLatestNearbyDating(String userId) {
        String hql = "from NearbyDating where userId = :userId and state = :state order by created desc";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("userId", userId);
        paramMap.put("state", NearbyDatingState.NORMAL.getCode());
        return baseRepository.executeHql(hql, paramMap);
    }

    /**
     * 根据附近有约id获取报名明细
     */
    private List<NearbyDatingApplyItem> queryApplyItem(Long nId, String userId) {
        StringBuilder hql = new StringBuilder(50);
        hql.append("from NearbyDatingApplyItem where nId = :nId ");
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("nId", nId);
        if (StringUtils.isNoneBlank(userId)) {
            hql.append(" and userId = :userId");
            paramMap.put("userId", userId);
        }
        hql.append(" order by created desc");
        return baseRepository.queryHqlResult(hql, paramMap, 0, CutPage.MAX_COUNT);
    }
}
