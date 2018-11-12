package com.war4.service.impl;

import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.FileUpload;
import com.war4.pojo.Gift;
import com.war4.repository.BaseRepository;
import com.war4.service.AssembleService;
import com.war4.service.GiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2016/12/13.
 */
@Service
public class GiftServiceImpl implements GiftService {

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private AssembleService assembleService;

    @Override
    @Transactional
    public Gift addGift(Gift gift) {
        if (gift.getGold() == null || gift.getExchangePrice() == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "价格不能为空");
        }
        if (gift.getGold().compareTo(BigDecimal.ZERO) <= 0 || gift.getExchangePrice().compareTo(BigDecimal.ZERO) < 0 || gift.getGold().compareTo(gift.getExchangePrice()) < 0) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "销售价格不能小于等于0、交易价格不能小于0，且交易价格不能大于销售价");
        }
        Gift g = new Gift();
        g.setId(UUID.randomUUID().toString());
        g.setName(gift.getName());
        g.setGold(gift.getGold());
        g.setExchangePrice(gift.getExchangePrice());
        g.setPhotoUrl(gift.getPhotoUrl());
        baseRepository.saveObj(g);
        return g;
    }

    @Override
    public CutPage<Gift> queryGift(Integer page, Integer limit) {
        String hql = "from Gift where delFlag = 0 order by gold asc";
        CutPage<Gift> cutPage = baseRepository.executeHql(hql, null, page, limit);
        List<String> ids = new ArrayList<>();
        for (Gift gift : cutPage.getiData()) {
            ids.add(gift.getId());
        }
        if (ids.size() > 0) {
            hql = "from FileUpload where fkPurposeId in(:ids) group by fkPurposeId order by field(fkPurposeId,:ids)";
            Map<String, Object> map = new HashMap<>();
            map.put("ids", ids);
            CutPage<FileUpload> cutPage2 = baseRepository.executeHql(hql, map, 0, ids.size());
            List<FileUpload> files = cutPage2.getiData();
            List<Gift> gifts = cutPage.getiData();
            for (int i = 0,y = 0; i < files.size(); i++) {
                FileUpload f = files.get(i);
                y = settingPic(gifts,f,y);
            }
        }
        return cutPage;
    }
    //设置图片
    private int settingPic(List<Gift> gifts, FileUpload file, int i){
        if (file.getFkPurposeId().equals(gifts.get(i).getId())) {
            gifts.get(i).setPhotoUrl(file.getLocation());
            return ++i;
        }
        return settingPic(gifts, file, ++i);
    }

    @Override
    public Gift queryGiftById(String giftId) {
        Gift gift = baseRepository.getObjById(Gift.class, giftId);
        if (gift == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "您要查找的礼物不存在!");
        }
        gift = assembleService.assembleObject(gift);
        return gift;
    }

    @Override
    @Transactional
    public void deleteGift(String giftId) {
        Gift gift = baseRepository.getObjById(Gift.class, giftId);
        if (gift == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "您要删除的礼物不存在!");
        }
        baseRepository.logicDelete(gift);
    }

}
