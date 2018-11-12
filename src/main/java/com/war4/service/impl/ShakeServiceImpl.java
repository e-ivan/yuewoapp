package com.war4.service.impl;

import com.war4.pojo.Shake;
import com.war4.pojo.UserInfoBase;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.ShakeService;
import com.war4.service.UserInfoBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/28.
 */
@Service
public class ShakeServiceImpl implements ShakeService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private UserInfoBaseService userInfoBaseService;

    @Override
    @Transactional
    public UserInfoBase saveShakeAndGetPerple(String fkUserId, String x, String y) throws InterruptedException {
        Shake selectObj  = baseRepository.getObjById(Shake.class,fkUserId);
        if(selectObj==null){
            Shake shake = new Shake();
            shake.setFkUserId(fkUserId);
            shake.setX(x);
            shake.setY(y);
            baseRepository.saveObj(shake);
        }else{
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Date date = new Date();
            try {
                date = ts;
                System.out.println(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            selectObj.setX(x);
            selectObj.setY(y);
            selectObj.setCreateTime(ts);
            baseRepository.saveObj(selectObj);
        }

        Thread.sleep(3000);

        String hql = baseRepository.getBaseHqlByClass(Shake.class);
        hql += " and fkUserId !='"+fkUserId+"'";
        hql += " order by createTime desc, ";
        hql +=" (POWER(MOD(ABS(X - "+x+"),360),2) + POWER(ABS(Y - "+y+"),2))  ";

        Shake getObj = baseRepository.queryUniqueData(hql);
        UserInfoBase userInfoBase = null;
        if(getObj!=null){
             userInfoBase = userInfoBaseService.getUserById(getObj.getFkUserId());
        }
        return userInfoBase;
    }

    public static void main(String[] args) throws Exception{
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date date = new Date();
        try {
            date = ts;
            Thread.sleep(3000);
            System.out.println(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
