package com.war4.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.base.SystemParameters;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.FilmRoomStatus;
import com.war4.enums.OrderType;
import com.war4.pojo.Film;
import com.war4.pojo.FilmRoom;
import com.war4.pojo.FilmRoomUser;
import com.war4.pojo.Gift;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.thirdParty.FFmpegCommandManager.FFmpegManager;
import com.war4.thirdParty.FFmpegCommandManager.FFmpegManagerImpl;
import com.war4.thirdParty.FFmpegCommandManager.entity.TaskEntity;
import com.war4.util.OrderUtil;
import com.war4.util.RongYunMessage.GiftMessage;
import com.war4.util.TimerTaskForUser;
import com.war4.util.WangYiUtil;
import net.sf.json.JSONObject;
import org.apache.http.Consts;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2016/12/30.
 */
@Service
public class FilmRoomServiceImpl implements FilmRoomService {


    public Logger logger = Logger.getLogger(FilmRoomServiceImpl.class.getName());

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private UserGiftService userGiftService;

    @Autowired
    private RongYunService rongYunService;

    @Autowired
    private UserVIPService userVIPService;

    @Autowired
    private BaseOrderService baseOrderService;

    @Autowired
    private PushPayloadService pushPayloadService;

    @Override
    @Transactional
    public FilmRoom addFilmRoom(FilmRoom filmRoom) throws Exception {

        //

        if(filmRoom.getFkUserId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"FkUserId不能为空");
        }
        if(filmRoom.getFkFilmId()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"FkFilmId不能为空");
        }
        if(filmRoom.getCount()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"房间人数count不能为空");
        }

        Film film = filmService.queryFilmById(filmRoom.getFkFilmId());

        filmRoom.setId(OrderUtil.getUUID());

        filmRoom.setStatus(FilmRoomStatus.CREATE.getCode());
        filmRoom.setTotalMoney(film.getPrice().multiply(new BigDecimal(filmRoom.getCount())));

        baseOrderService.addBaseOrder(filmRoom.getId(), OrderType.FILM_ROOM,filmRoom.getFkUserId());

        baseRepository.saveObj(filmRoom);


        return filmRoom;
    }



    @Override
    @Transactional
    public void closeFilmRoon(String roomId) throws Exception {
        FilmRoom filmRoom = baseRepository.getObjById(FilmRoom.class,roomId);
        if(filmRoom==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到该房间！");
        }
        String url = "https://vcloud.163.com/app/channel/delete";

        // 设置请求的参数
        StringEntity params = new StringEntity("{\"cid\":\""+filmRoom.getCid()+"\", \"type\":0}", Consts.UTF_8);

        // 打印执行结果
        String result = null;
        try {
            result = WangYiUtil.commonPost(url,params);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"删除直播失败！");
        }
        System.out.println(result);
        JSONObject jsonObject = JSONObject.fromObject(result);
        System.out.println(jsonObject.toString());
        String code = jsonObject.getString("code");
        if(!code.equals("200")){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"删除直播失败！");
        }
        rongYunService.destroyChatRoom(filmRoom.getId());

        baseRepository.logicDelete(filmRoom);

        FFmpegManagerImpl manager = FFmpegManagerImpl.getInstance();
        manager.stop(filmRoom.getId());
    }

    //加入房间
    @Override
    @Transactional
    public void joinFilmRoom(String fkUserId, String fkRoomId) {
        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkUserId不能为空！");
        }
        if(fkRoomId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkRoomId不能为空！");
        }
        FilmRoom filmRoom = queryFilmRoom(fkRoomId);
        CutPage cut = queryFilmRoomUsers(fkRoomId);
        if(queryFilmRoomUser(fkUserId,fkRoomId)!=null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"您已经加入该房间！");
        }
        if(filmRoom==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"找不到该房间！");
        }
        if(fkUserId.equals(filmRoom.getFkUserId())){
            return;
        }
        if(cut.getTotalCount()>=filmRoom.getCount().longValue()){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该房间已满！");
        }
        FilmRoomUser filmRoomUser  = new FilmRoomUser();
        filmRoomUser.setId(UUID.randomUUID().toString());
        filmRoomUser.setFkRoomId(fkRoomId);
        filmRoomUser.setFkUserId(fkUserId);
        baseRepository.saveObj(filmRoomUser);
    }

    @Override
    @Transactional
    public void updateFilmRoomPid(String roomId, String pid) {
        FilmRoom filmRoom = queryFilmRoom(roomId);
        if(filmRoom!=null){
            filmRoom.setPid(pid);
            baseRepository.updateObj(filmRoom);
        }
    }

    @Override
    public FilmRoomUser queryFilmRoomUser(String fkUserId, String fkRoomId) {
        String hql = baseRepository.getBaseHqlByClass(FilmRoomUser.class);
        hql += " and fkRoomId ='"+fkRoomId+"' ";
        hql += " and fkUserId ='"+fkUserId+"' ";
        FilmRoomUser filmRoomUser = baseRepository.queryUniqueData(hql);
        return filmRoomUser;
    }

    @Override
    @Transactional
    public void deleteFilmRoomUser(String fkUserId, String fkRoomId) throws Exception {
        FilmRoom filmRoom = queryFilmRoom(fkRoomId);
        if(filmRoom==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该房间已满！");
        }
        if(fkUserId.equals(filmRoom.getFkUserId())){
            closeFilmRoon(fkRoomId);
            return;
        }
        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkUserId不能为空！");
        }
        if(fkRoomId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkRoomId不能为空！");
        }
        FilmRoomUser filmRoomUser = queryFilmRoomUser(fkUserId, fkRoomId);
        if(filmRoomUser!=null){
            baseRepository.logicDelete(filmRoomUser);
        }
    }

    @Override
    public void addAnchorUserGift(String roomId, String fkUserId, String giftId, Integer count) throws Exception {
        if(roomId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"roomId不能为空！");
        }
        if(fkUserId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"fkUserId不能为空！");
        }
        if(giftId==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"giftId不能为空！");
        }
        if(count==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"count不能为空！");
        }
        FilmRoom filmRoom = queryFilmRoom(roomId);
        Gift gift = giftService.queryGiftById(giftId);
        if(gift!=null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的礼物！");
        }
        userGiftService.addUserGift(fkUserId, filmRoom.getFkUserId(), giftId, count);

        GiftMessage giftMessage = new GiftMessage("GIFT:"+giftId,gift.getName());
        rongYunService.publishChatroom(SystemParameters.SYSTEM_USER_ID,filmRoom.getId(),giftMessage);
    }

    @Override
    public FilmRoom queryFilmRoom(String roomId) {
        FilmRoom filmRoom = baseRepository.getObjById(FilmRoom.class,roomId);
        if(filmRoom!=null){
            filmRoom = assembleService.assembleObject(filmRoom);
        }

        return filmRoom;
    }

    @Override
    public CutPage<FilmRoom> queryFileRoomList(String name, Integer page, Integer limit) {

        String hql = baseRepository.getBaseHqlByClass(FilmRoom.class);
        if(!StringUtils.isEmpty(name)){
            hql += " and name like '%"+name+"%'";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());//date 换成已经已知的Date对象
        cal.add(Calendar.HOUR_OF_DAY, -3);// before 3 hour
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strTime=sdf.format(cal.getTime());
        hql += " and status = "+FilmRoomStatus.PAY_SUCCESS.getCode()+" and play_time < '"+strTime+"' ORDER BY create_time DESC";
        CutPage<FilmRoom> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(FilmRoom filmRoom:cutPage.getiData()){
            filmRoom = assembleService.assembleObject(filmRoom);
        }
        return cutPage;
    }



    @Override
    public CutPage<FilmRoomUser> queryFilmRoomUsers(String roomId) {
        String hql = baseRepository.getBaseHqlByClass(FilmRoomUser.class);
        hql += " and fkRoomId ='"+roomId+"'ORDER BY createTime DESC";
        CutPage<FilmRoomUser> cut = new CutPage<>(0,CutPage.MAX_COUNT);
        cut = baseRepository.queryCutPageData(hql,cut);
        for (FilmRoomUser filmUser:cut.getiData()){
            filmUser = assembleService.assembleObject(filmUser);
        }
        return cut;
    }

    @Override
    public Integer queryFilmRoomUsersCount(String roomId) {
        String hql = baseRepository.getBaseHqlByClass(FilmRoomUser.class);
        hql += " and fkRoomId ='"+roomId+"'";
        Long count = baseRepository.getTotalCount(hql);
        return count.intValue();
    }
    /*
        在研究异步处理之前，我在想为什么update方法没有在这个方法结束之前运行但是没有找到原因
        如果有后来研究的同道，我们可以一起探讨一下（QQ:805801525）
        但我是知道的，这个方法不结束之前是不会操作任何有关数据库变更的操作，
        所以我多线程处理的实现方法，将这个需要长时间等待的视频转推流流程放在异步线程
        结果达到的预期的效果
     */
    @Override
    @Transactional
    public void updateFilmRoomStatus(String roomId) throws Exception {
        FilmRoom filmRoom = baseRepository.getObjById(FilmRoom.class,roomId);
        if(filmRoom!=null){

            //先找到该电影
            Film film = filmService.queryFilmById(filmRoom.getFkFilmId());

            //去网易开一个直播间
            String url = "https://vcloud.163.com/app/channel/create";

            // 设置请求的参数
            StringEntity params = new StringEntity("{\"name\":\""+filmRoom.getName()+"\", \"type\":0}", Consts.UTF_8);

            // 打印执行结果
            String result = null;
            try {
                result = WangYiUtil.commonPost(url,params);
            } catch (Exception e) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"开启直播失败！");
            }
            System.out.println(result);
            JSONObject jsonObject = JSONObject.fromObject(result);
            System.out.println(jsonObject.toString());
            String code = jsonObject.getString("code");
            if(!code.equals("200")){
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"开启直播失败！");
            }
            System.out.println("code = "+code);
            JSONObject retObj  = jsonObject.getJSONObject("ret");
            String httpPullUrl = retObj.getString("httpPullUrl");
            System.out.println(httpPullUrl);
            String hlsPullUrl = retObj.getString("hlsPullUrl");
            System.out.println(hlsPullUrl);
            String rtmpPullUrl = retObj.getString("rtmpPullUrl");
            System.out.println(rtmpPullUrl);
            String pushUrl = retObj.getString("pushUrl");
            System.out.println(pushUrl);
            String cid = retObj.getString("cid");
            System.out.println(cid);
            filmRoom.setCid(cid);
            filmRoom.setPushUrl(pushUrl);
            filmRoom.setHttpPullUrl(httpPullUrl);
            filmRoom.setHlsPullUrl(hlsPullUrl);
            filmRoom.setRtmpPullUrl(rtmpPullUrl);

            //修改房间的支付状态
            filmRoom.setStatus(FilmRoomStatus.PAY_SUCCESS.getCode());
            //修改变更状态
            baseRepository.updateObj(filmRoom);
            //添加累计消费
            userVIPService.addIntegral(filmRoom.getFkUserId(),filmRoom.getTotalMoney().intValue());
            //开通融云聊天室
            rongYunService.createChatRoom(filmRoom.getId(),filmRoom.getName());

            ExecutorService executor = Executors.newCachedThreadPool();
            try {
                //异步处理
                //异步处理相关文档：http://www.cnblogs.com/jevo/archive/2013/04/11/3015023.html
                FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
                    public String call() throws Exception { //建议抛出异常
                        try {
                            Date playDate = new Date(new Long(filmRoom.getPlayTime()));

                            //开启任务，到点开始播放电影
                            Timer timer = new Timer();

                            timer.schedule(new TimerTaskForUser() {
                                @Transactional
                                public void run() {
                                    FFmpegManager manager = FFmpegManagerImpl.getInstance();
                                    Map map = new HashMap();
                                    map.put("appName", filmRoom.getId());
                                    map.put("input", PropertiesConfig.getFileRoot() + film.getFilmUrl().replace("/staticFile", ""));
                                    map.put("output", pushUrl);

                                    // 执行任务，id就是appName，如果执行失败返回为null
                                    String id = manager.start(map);
                                    System.out.println("任务id为：" + id);

                                    // 通过id查询
                                    TaskEntity info = manager.query(id);
                                    System.out.println(info);
                                    // 查询全部
                                    Collection<TaskEntity> infoList = manager.queryAll();
                                    System.out.println(infoList);
                                }

                            }, playDate);
                            Long surplusTime = playDate.getTime() - new Date().getTime();
                            //播放时间差 + 睡眠三个小时 之后，关闭timer
                            Thread.sleep(surplusTime + 1000 * 60 * 60 * 3);
                            timer.cancel();

                            //推送

                        } catch (Exception e) {
                            throw new Exception("Callable terminated with Exception!"); // call方法可以抛出异常
                        }
                        return "YES";
                    }
                });

                executor.execute(future);
            }catch (Exception e){
                System.out.println(e.toString());
            }finally {
                //线程池必须关闭
                executor.shutdown();
            }
        }
    }

    public static void main(String[] args){
        ExecutorService executor = Executors.newCachedThreadPool();
        FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
            public String call() throws Exception{ //建议抛出异常
                try {
                    Thread.sleep(5* 1000);
                    return "Hello Welcome!";
                }
                catch(Exception e) {
                    throw new Exception("Callable terminated with Exception!"); // call方法可以抛出异常
                }
            }
        });
        executor.execute(future);
        System.out.println("111");
    }
}
