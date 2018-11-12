package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.SystemParameters;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.UserRole;
import com.war4.pojo.*;
import com.war4.repository.BaseRepository;
import com.war4.service.*;
import com.war4.util.CheckSumBuilder;
import com.war4.util.OrderUtil;
import com.war4.util.RongYunMessage.GiftMessage;
import com.war4.util.WangYiUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2016/12/7.
 */
@Service
public class AnchorRoomServiceImpl implements AnchorRoomService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private RongYunService rongYunService;

    @Autowired
    private UserGiftService userGiftService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private     UserInfoBaseServiceImpl userInfoBaseService;

    @Override
    @Transactional
    public AnchorRoom addAnchorRoom(String userId, String name) throws Exception {
        UserInfoBase userInfoBase = userInfoBaseService.getUserById(userId);
        if(userInfoBase==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"该用户不存在！");
        }
        if(userInfoBase.getRole()==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"您的个人信息不完善，请完善角色信息！");
        }
        if(checkUserHaveAnchor(userId)){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"开启直播失败,您已经开启了直播！");
        }

        String url = "https://vcloud.163.com/app/channel/create";

        // 设置请求的参数
        StringEntity params = new StringEntity("{\"name\":\""+name+"\", \"type\":0}", Consts.UTF_8);

        // 打印执行结果
        String result = null;
        try {
            result = WangYiUtil.commonPost(url,params);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"开启直播失败！");
        }
        JSONObject jsonObject = JSONObject.fromObject(result);
        String code = jsonObject.getString("code");
        if(!code.equals("200")){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"开启直播失败！");
        }

        JSONObject retObj  = jsonObject.getJSONObject("ret");
        String httpPullUrl = retObj.getString("httpPullUrl");
        String hlsPullUrl = retObj.getString("hlsPullUrl");
        String rtmpPullUrl = retObj.getString("rtmpPullUrl");
        String pushUrl = retObj.getString("pushUrl");
        String cid = retObj.getString("cid");


        AnchorRoom anchorRoom = new AnchorRoom();
        anchorRoom.setId(UUID.randomUUID().toString());
        anchorRoom.setFkUserId(userId);
        anchorRoom.setCid(cid);
        anchorRoom.setPushUrl(pushUrl);
        anchorRoom.setRtmpPullUrl(rtmpPullUrl);
        anchorRoom.setHlsPullUrl(hlsPullUrl);
        anchorRoom.setHttpPullUrl(httpPullUrl);
        anchorRoom.setName(name);
        anchorRoom.setTotal(0);
        anchorRoom.setChatRoomId(UUID.randomUUID().toString());
        anchorRoom.setRole(userInfoBase.getRole());

        rongYunService.createChatRoom(anchorRoom.getChatRoomId(),anchorRoom.getName());

        baseRepository.saveObj(anchorRoom);

        return anchorRoom;
    }

    @Override
    @Transactional
    public void operationRecording(String userId,String roomId, Integer needRecord, Integer format, Integer duration,String filename) {
        String url = "https://vcloud.163.com/app/channel/setAlwaysRecord";
        AnchorRoom anchorRoom=baseRepository.getObjById(AnchorRoom.class,roomId);
        // 设置请求的参数
        StringEntity params = new StringEntity("{\"cid\":\""+anchorRoom.getCid()+"\", \"needRecord\":\"" + needRecord + "\",\"format\":\"" + format + "\"," + "\"duration\":\"" + duration + "\"," +"\"filename\":\"" +filename + "\"}", Consts.UTF_8);

        // 打印执行结果
        String result = null;
        try {
            result = WangYiUtil.commonPost(url,params);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"失败");
        }

//       if (needRecord==0){
//           String urls = "https://vcloud.163.com/app/videolist";
//           AnchorRoom anchorRoom1=baseRepository.getObjById(AnchorRoom.class,roomId);
//           // 设置请求的参数
//           StringEntity params1 = new StringEntity("{\"cid\":\""+anchorRoom1.getCid()+"\"}", Consts.UTF_8);
//           // 打印执行结果
//           String result1 = null;
//           try {
//               result1 = WangYiUtil.commonPost(urls,params1);
//               JSONObject jsonObject = JSONObject.fromObject(result1);
//               String code=jsonObject.getString("code");
//               if (code.equals("200")){
//                   String ret=jsonObject.getString("ret");
//                   JSONObject jsonObject1 = JSONObject.fromObject(ret);
//                   String videoList=jsonObject1.getString("videoList");
//                   //System.out.print(videoList);
//                   JSONArray jsonArray= JSONArray.fromObject(videoList);
//                   if(jsonArray.size()>0){
//                       for(int i=0;i<jsonArray.size();i++){
//                           JSONObject job = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
//                             // 得到 每个对象中的属性值
//                           String name=job.getString("video_name");
//                           String videoLocation=job.getString("orig_video_key");
//                           UserTeacherVideo userTeacherVideo=new UserTeacherVideo();
//                           userTeacherVideo.setId(OrderUtil.getUUID());
//                           userTeacherVideo.setVideoName(name);
//                           userTeacherVideo.setVideoLocation(videoLocation);
//                           userTeacherVideo.setUserId(userId);
//                           baseRepository.saveObj(userTeacherVideo);
//                       }
//                   }
//
//               }
//           } catch (Exception e) {
//               throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"失败");
//           }
//
//       }
    }

    @Override
    @Transactional
    public void setVoide(String cid,String userId) {
            System.out.print("进来了...........");
            String urls = "https://vcloud.163.com/app/videolist";
            //AnchorRoom anchorRoom1=baseRepository.getObjById(AnchorRoom.class,roomId);
            // 设置请求的参数
            StringEntity params1 = new StringEntity("{\"cid\":\""+cid+"\"}", Consts.UTF_8);
            // 打印执行结果
            String result = null;
            try {
                result = WangYiUtil.commonPost(urls,params1);

               // System.out.println(result);

                JSONObject jsonObject = JSONObject.fromObject(result);
              //  System.out.print("进行中..........");
                String code=jsonObject.getString("code");
                System.out.print(code);
                System.out.print(cid);
                if (code.equals("200")){
                    String ret=jsonObject.getString("ret");
                    JSONObject jsonObject1 = JSONObject.fromObject(ret);
                    String videoList=jsonObject1.getString("videoList");
                    //System.out.print(videoList);
                    JSONArray jsonArray= JSONArray.fromObject(videoList);
                   // System.out.println(jsonArray.size()>0);
                    if(jsonArray.size()<=0){
                        setVoide(cid,userId);
                    }
                    if(jsonArray.size()>0){
                        for(int i=0;i<jsonArray.size();i++){
                            System.out.print("进来了22222...........");

                            JSONObject job = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                            // 得到 每个对象中的属性值
                            String name=job.getString("video_name");
                            String videoLocation=job.getString("orig_video_key");
                            UserTeacherVideo userTeacherVideo=new UserTeacherVideo();
                            userTeacherVideo.setId(OrderUtil.getUUID());
                            userTeacherVideo.setVideoName(name);
                            userTeacherVideo.setVideoLocation(videoLocation);
                            userTeacherVideo.setUserId(userId);
                            baseRepository.saveObj(userTeacherVideo);
                        }
                    }

                }
               // System.out.print("结束了............");
            } catch (Exception e) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"失败");
            }


    }

    @Override
    @Transactional
    public void closeAnchorRoom(String roomId) throws Exception {
        AnchorRoom anchorRoom = baseRepository.getObjById(AnchorRoom.class,roomId);
        if(anchorRoom==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的直播！");
        }
        String url = "https://vcloud.163.com/app/channel/delete";

        // 设置请求的参数
        StringEntity params = new StringEntity("{\"cid\":\""+anchorRoom.getCid()+"\", \"type\":0}", Consts.UTF_8);

        // 打印执行结果
        String result = null;
        try {
            result = WangYiUtil.commonPost(url,params);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"删除直播失败！");
        }

        JSONObject jsonObject = JSONObject.fromObject(result);
        String code = jsonObject.getString("code");
        if(!code.equals("200")){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"删除直播失败！");
        }
        rongYunService.destroyChatRoom(anchorRoom.getChatRoomId());

        baseRepository.logicDelete(anchorRoom);
    }

    @Override
    @Transactional
    public void updateAnchorRoom(String roomId, String name) {
        AnchorRoom anchorRoom = baseRepository.getObjById(AnchorRoom.class,roomId);
        if(anchorRoom==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的直播！");
        }
        String url = "https://vcloud.163.com/app/channel/update";

        // 设置请求的参数
        StringEntity params = new StringEntity("{\"cid\":\""+anchorRoom.getCid()+"\",\"name\":\""+name+"\", \"type\":0}", Consts.UTF_8);
        // 打印执行结果
        String result = null;
        try {
            result = WangYiUtil.commonPost(url,params);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"修改直播失败！");
        }
        JSONObject jsonObject = JSONObject.fromObject(result);
        String code = jsonObject.getString("code");
        if(!code.equals("200")){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"修改直播失败！");
        }

        anchorRoom.setName(name);
        baseRepository.updateObj(anchorRoom);
    }

    @Override
    public Integer queryRoomPeopleCount(String roomId) throws Exception {
        AnchorRoom anchorRoom = baseRepository.getObjById(AnchorRoom.class,roomId);
        if(anchorRoom==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"直播已关闭！");
        }
        return  rongYunService.queryChatRoom(anchorRoom.getChatRoomId());
    }

    @Override
    public AnchorRoom queryAnchorRoom(String roomId) {
        AnchorRoom anchorRoom = baseRepository.getObjById(AnchorRoom.class,roomId);
        if(anchorRoom==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的直播！");
        }
        anchorRoom = assembleService.assembleObject(anchorRoom);
        return anchorRoom;
    }

    @Override
    public AnchorRoom getAnchorRoomByUserId(String userId) {
        String hql = baseRepository.getBaseHqlByClass(AnchorRoom.class);
        hql +=" and fkUserId = '"+userId+"' ";
        AnchorRoom anchorRoom = baseRepository.queryUniqueData(hql);
        if(anchorRoom==null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"直播未开始！");
        }
        anchorRoom = assembleService.assembleObject(anchorRoom);
        return anchorRoom;
    }

    @Override
    @Transactional
    public void joinAnchorRoom(String roomId,String userId) throws Exception {
        AnchorRoom anchorRoom = baseRepository.getObjById(AnchorRoom.class,roomId);
        if(anchorRoom==null){
            return ;
        }
        rongYunService.joinChatRoom(userId,anchorRoom.getChatRoomId());

        updateAnchorRoomPeopleTotal(anchorRoom);



//        GiftMessage giftMessage = new GiftMessage("PEOPLE:",total.toString());


    }



    @Override
    @Transactional
    public void editAnchorRoom(String roomId, String userId) throws Exception {
    }

    @Override
    @Transactional
    public void updateAnchorRoomPeopleTotal(AnchorRoom anchorRoom) throws Exception {
        if(anchorRoom==null){
            return ;
        }
        Integer total = rongYunService.queryChatRoom(anchorRoom.getChatRoomId());
        anchorRoom.setTotal(total);
        baseRepository.updateObj(anchorRoom);
    }

    @Override
    @Transactional
    public void addAnchorUserGift(String roomId,String fkUserId,  String giftId, Integer count) throws Exception {
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
        AnchorRoom anchorRoom = queryAnchorRoom(roomId);
        Gift gift = giftService.queryGiftById(giftId);
        if(gift!=null){
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的礼物！");
        }
        userGiftService.addUserGift(fkUserId, anchorRoom.getFkUserId(), giftId, count);
        UserInfoBase userInfoBase=baseRepository.getObjById(UserInfoBase.class,fkUserId);
        userInfoBase.setGiftValue(userInfoBase.getGiftValue().add(gift.getGold().multiply(new BigDecimal(count))));
        baseRepository.updateObj(userInfoBase);
        GiftMessage giftMessage = new GiftMessage("GIFT:"+giftId,gift.getName());

        rongYunService.publishChatroom(SystemParameters.SYSTEM_USER_ID,anchorRoom.getChatRoomId(),giftMessage);
    }

    @Override
    public boolean checkUserHaveAnchor(String userId) {
        boolean flag = false;
        String hql = baseRepository.getBaseHqlByClass(AnchorRoom.class);
        hql +=" and fkUserId = '"+userId+"' ";
        AnchorRoom anchorRoom = baseRepository.queryUniqueData(hql);
        if(anchorRoom!=null){
            flag = true;
        }
        return flag;
    }

    @Override
    public CutPage<AnchorRoom> queryAnchorRooms(Integer type, Integer page, Integer limit) {

        CutPage<AnchorRoom> cutPage = new CutPage<>(page, limit);
        String hql = baseRepository.getBaseHqlByClass(AnchorRoom.class);
        hql+="";
        cutPage = baseRepository.queryCutPageData(hql,cutPage);
        for(AnchorRoom anchorRoom:cutPage.getiData()){
            anchorRoom = assembleService.assembleObject(anchorRoom);
        }
        return cutPage;
    }

    /*
        测试sql
        SELECT room.id FROM anchor_room room INNER JOIN anchor_fan fan ON
        room.fk_user_id = fan.anchor_user_id WHERE room.`del_flag` = 0 AND
        fan.`del_flag` = 0 AND fan.`fan_user_id` = "148177328499998757";

     */
    @Override
    public CutPage<AnchorRoom> queryMyAttentionAnchor(String userId, Integer role , Integer page, Integer limit) {

        String sql  = " FROM anchor_room room INNER JOIN anchor_fan fan ON " +
                "room.fk_user_id = fan.anchor_user_id INNER JOIN user_info_base USER  ON fan.anchor_user_id = user.id  WHERE room.del_flag = 0 AND " +
                "fan.del_flag = 0 AND fan.fan_user_id = '"+userId+"' ";

        if(role!=null){
            if(UserRole.getByCode(role)==null){
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的角色！");
            }
            sql += " and user.role = "+role+" ";
        }

        CutPage<AnchorRoom> cutPage = new CutPage<>(page, limit);
        CutPage<String> stringCutPage = new CutPage<>(page, limit);
        stringCutPage = baseRepository.querySQLCutPageData("SELECT room.id ","select count(room.id) ",sql,stringCutPage);
        for(String roomId:stringCutPage.getiData()){
            AnchorRoom  anchorRoom = queryAnchorRoom(roomId);
            cutPage.getiData().add(anchorRoom);
        }

        return cutPage;

    }

    @Override
    public CutPage<AnchorRoom> queryHostAnchor(Integer role,Integer page, Integer limit) {

        String sql = baseRepository.getBaseHqlByClass(AnchorRoom.class);
        if(role!=null){
            if(UserRole.getByCode(role)==null){
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的角色！");
            }
            sql += " and role = "+role+" ";
        }
        sql += "order by total desc ";
        CutPage<AnchorRoom> cutPage = new CutPage<>(page, limit);
        cutPage = baseRepository.queryCutPageData(sql,cutPage);
        for(AnchorRoom anchorRoom:cutPage.getiData()){
            anchorRoom = assembleService.assembleObject(anchorRoom);
        }
        return cutPage;
    }

    /*
        测试sql
        SELECT room.id FROM anchor_room room INNER JOIN
        user_info_base USER ON room.fk_user_id = user.id
        WHERE room.`del_flag` = 0 AND user.`del_flag` = 0
        ORDER BY (POWER(MOD(ABS(user.X - 30 ),360),2) + POWER(ABS(user.Y - 70),2))

     */
    @Override
    public CutPage<AnchorRoom> queryNearAnchor(String x, String y,Integer role , Integer sex,Integer page, Integer limit) {


        String sql = " FROM anchor_room room INNER JOIN user_info_base USER ON " +

                "room.fk_user_id = user.id WHERE room.del_flag = 0 AND user.del_flag = 0" ;
        if(role!=null){
            if(UserRole.getByCode(role)==null){
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"不存在的角色！");
            }
            sql += " and user.role = "+role+" ";
        }
        if(sex!=null){
            sql += " and user.sex = "+sex+" ";
        }
        sql +=" ORDER BY (POWER(MOD(ABS(user.X - "+x+"),360),2) + POWER(ABS(user.Y - "+y+"),2))  ";

        CutPage<AnchorRoom> cutPage = new CutPage<>(page, limit);
        CutPage<String> stringCutPage = new CutPage<>(page, limit);
        stringCutPage = baseRepository.querySQLCutPageData("SELECT room.id ","select count(room.id) ",sql,stringCutPage);
        for(String roomId:stringCutPage.getiData()){
            AnchorRoom  anchorRoom = queryAnchorRoom(roomId);
            cutPage.getiData().add(anchorRoom);
        }
        return cutPage;

    }

    @Override
    @Transactional
    public Trailer addTrailer(String userId, String startTime, String title, String content) {
        Trailer trailer=new Trailer();
        trailer.setId(OrderUtil.getUUID());
        trailer.setUserId(userId);
        trailer.setContent(content);
        trailer.setTitle(title);
        trailer.setStartTime(startTime);
        baseRepository.saveObj(trailer);
        return trailer;
    }

    @Override
    @Transactional
    public CutPage<Trailer> queryTrailerList(String userId, Integer page, Integer limit) {
        String hql=baseRepository.getBaseHqlByClass(Trailer.class);
        CutPage<Trailer>cutPage=new CutPage<>(page, limit);
        cutPage=baseRepository.queryCutPageData(hql,cutPage);
        for (Trailer trailer:cutPage.getiData()){
            Date nowDate=new Date();
            Long nowTime=nowDate.getTime();
            SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long startTime= null;
            try {
                startTime = sdf.parse(trailer.getStartTime()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (nowTime>startTime){
                baseRepository.logicDelete(trailer);
            }
        }
        for (Trailer trailer:cutPage.getiData()){
            UserInfoBase userInfoBase=baseRepository.getObjById(UserInfoBase.class,trailer.getUserId());
            assembleService.assembleObject(userInfoBase);
            trailer.setUserInfoBase(userInfoBase);
            assembleService.assembleObject(trailer);
        }
        return cutPage;
    }

    public static void main(String[] args) throws Exception{
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "https://vcloud.163.com/app/channel/create";
        HttpPost httpPost = new HttpPost(url);

        String appKey = SystemParameters.WANGYI_APP_KEY;
        String appSecret = SystemParameters.WANGYIAPP_SECRET;
        String nonce =  "1";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");

        // 设置请求的参数
        StringEntity params = new StringEntity("{\"name\":\"滑稽表情包sdfsdfsdfsdf\", \"type\":0}",Consts.UTF_8);
        httpPost.setEntity(params);

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println(result);
        JSONObject jsonObject = JSONObject.fromObject(result);
        System.out.println(jsonObject.toString());
        String code = jsonObject.getString("code");
        if(!code.equals("200")){
            return ;
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

    }

    @Override
    @Transactional
    public AnchorRoom updateAnchorRoom(String id, Integer type) {
        AnchorRoom anchorRoom=baseRepository.getObjById(AnchorRoom.class,id);
        anchorRoom.setRole(type);
        baseRepository.updateObj(anchorRoom);
        return anchorRoom;
    }

    @Override
    public Trailer queryTrailer(String id) {
        Trailer trailer=baseRepository.getObjById(Trailer.class,id);
        assembleService.assembleObject(trailer);
        UserInfoBase userInfoBase=baseRepository.getObjById(UserInfoBase.class,trailer.getUserId());
        assembleService.assembleObject(userInfoBase);
        trailer.setUserInfoBase(userInfoBase);
        return trailer;
    }

    @Override
    public String findAnchorRoom(String roomId) {
        AnchorRoom anchorRoom=baseRepository.getObjById(AnchorRoom.class,roomId);
        assembleService.assembleObject(anchorRoom);
        return anchorRoom.getPhotoUrl();
    }
}
