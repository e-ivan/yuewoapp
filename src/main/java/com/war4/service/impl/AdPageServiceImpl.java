package com.war4.service.impl;

import com.alibaba.fastjson.JSON;
import com.war4.base.BaseService;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.*;
import com.war4.pojo.AdPage;
import com.war4.service.AdPageService;
import com.war4.util.FileUploadUtils;
import com.war4.vo.AdPageImageNameVO;
import com.war4.vo.AdPageParamVO;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by E_Iva on 2018.3.29.0029.
 */
@Service
public class AdPageServiceImpl extends BaseService implements AdPageService {
    @Override
    @Transactional
    public void createAdPage(AdPageParamVO vo) {
        AdPage adPage;
        boolean isNew = vo.getAdId() == null;
        if (isNew) {
            adPage = new AdPage();
        }else {
            adPage = baseRepository.getObjById(AdPage.class,vo.getAdId());
            if (adPage == null) {
                throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"宣传页面不存在");
            }
        }
        adPage.setType(0);//重置属性
        adPage.setTitle(vo.getTitle());
        adPage.setContent(vo.getContent());
        adPage.setShowStartTime(vo.getStartTime() == null ? new Date() : vo.getStartTime());
        adPage.setShowEndTime(vo.getEndTime());
        adPage.setState(AdPageState.SHOW.getCode());
        adPage.setLocation(vo.getLocation());
        for (Integer code : vo.getTypes()) {
            adPage.setValue(vo.getValue());
            AdPageTypeVar typeVar = AdPageTypeVar.getByCode(code);
            if (typeVar != null) {
                adPage.addType(typeVar);
            }
        }
        if (isNew) {
            baseRepository.saveObj(adPage);
        }
        //保存图片
        try {
            AdPageImageNameVO images = adPage.getImage();
            if (isNew) {
                images = new AdPageImageNameVO();
            }else {
                //不是轮播删除轮播图，不是弹窗删除弹窗图
                if (!adPage.isAuctionBanner() && !adPage.isHomeBanner()) {
                    images.setBanner(null);
                    images.setShare(null);
                }
                if (!adPage.isHomePop()) {
                    images.setPop(null);
                }
            }
            if (vo.getBanner() != null) {
                images.setBanner(fileService.saveFile(FileTypeAndPath.IMAGE_TYPE_PATH, FilePurpose.AD_PAGE_IMAGE, adPage.getId().toString(), vo.getBanner()).get(0));
            }
            if (vo.getShare() != null) {
                images.setShare(fileService.saveFile(FileTypeAndPath.IMAGE_TYPE_PATH, FilePurpose.AD_PAGE_IMAGE, adPage.getId().toString(), vo.getShare()).get(0));
            }
            if (vo.getPop() != null) {
                images.setPop(fileService.saveFile(FileTypeAndPath.IMAGE_TYPE_PATH, FilePurpose.AD_PAGE_IMAGE, adPage.getId().toString(), vo.getPop()).get(0));
            }
            adPage.setImages(JSON.toJSONString(images));
            baseRepository.updateObj(adPage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "图片上传失败");
        }
    }

    @Override
    public List<AdPage> queryAdPage(Integer type) {
        String hql = "from AdPage where state = :state and bitand(type,:type) != 0 and showStartTime <= :now and (showEndTime >= :now or showEndTime is null) order by created desc";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("state",AdPageState.SHOW.getCode());
        AdPageTypeVar typeVar = AdPageTypeVar.getByCode(type);
        if (typeVar == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "查询类型不存在");
        }
        paramMap.put("type", typeVar.getTypeValue());
        paramMap.put("now", DateUtils.truncate(new Date(), Calendar.DATE));
        List<AdPage> adPages = baseRepository.queryHqlResult(hql, paramMap, 0, CutPage.MAX_COUNT);
        //处理图片
        adPages.forEach(a -> {
            AdPageImageNameVO image = JSON.parseObject(a.getImages(), AdPageImageNameVO.class);
            if (image != null) {
                image.setBanner(FileUploadUtils.removeImageSuffix(image.getBanner()));
                image.setPop(FileUploadUtils.removeImageSuffix(image.getPop()));
            }
            a.setImages(JSON.toJSONString(image));
        });
        return adPages;
    }

    @Override
    public CutPage<AdPage> queryAllAdPage(Integer state, Integer type, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(100);
        Map<String, Object> paramMap = baseRepository.paramMap();
        hql.append("from AdPage where state in :state ");
        if (state == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"状态不能为空");
        }
        switch (state){
            case 0 ://已结束的
                hql.append(" or (state = :show and showEndTime < :endTime)");
                paramMap.put("state", AdPageState.NO_SHOW.getCode());
                paramMap.put("endTime", DateUtils.truncate(new Date(), Calendar.DATE));
                paramMap.put("show",  AdPageState.SHOW.getCode());
                break;
            case 1://已开始或未开始，且未结束的
                hql.append(" and (showEndTime >= :endTime or showEndTime = null)");
                paramMap.put("endTime", DateUtils.truncate(new Date(), Calendar.DATE));
                paramMap.put("state",AdPageState.SHOW.getCode());
                break;

        }
        AdPageTypeVar typeVar = AdPageTypeVar.getByCode(type);
        if (typeVar != null) {
            hql.append(" and bitand(type,:type) != 0 ");
            paramMap.put("type",typeVar.getTypeValue());
        }
        hql.append(" order by created desc");
        return baseRepository.executeHql(hql,paramMap,page,limit);
    }

    @Override
    @Transactional
    public void updateAdPageState(Long adId,boolean state) {
        AdPage adPage = baseRepository.getObjById(AdPage.class, adId);
        if (adPage == null) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"宣传页面不存在");
        }
        adPage.setState(state ? AdPageState.SHOW.getCode() : AdPageState.NO_SHOW.getCode());
        baseRepository.updateObj(adPage);
    }
}
