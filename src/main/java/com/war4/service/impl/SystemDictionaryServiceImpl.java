package com.war4.service.impl;

import com.alibaba.fastjson.JSON;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonErrorResult;
import com.war4.pojo.SystemDictionary;
import com.war4.pojo.SystemDictionaryItem;
import com.war4.repository.BaseRepository;
import com.war4.service.SystemDictionaryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hh on 2017.6.24 0024.
 */
@Service
public class SystemDictionaryServiceImpl implements SystemDictionaryService {
    @Autowired
    private BaseRepository baseRepository;

    @Override
    @Transactional
    public void addSystemDictionary(SystemDictionary systemDictionary) {
        this.checkSnUnique(systemDictionary);
        baseRepository.saveObj(systemDictionary);
    }

    @Override
    @Transactional
    public void addSystemDictionaryItem(SystemDictionaryItem item) {
        checkExpression(item.getExpression());
        baseRepository.saveObj(item);
    }

    @Override
    @Transactional
    public void deleteSystemDictionary(SystemDictionary systemDictionary) {
        //获取明细一并删除
        List<SystemDictionaryItem> itemList = querySystemDictionaryItemList(null, null, systemDictionary.getId()).getiData();
        if (itemList.size() > 0) {
            for (SystemDictionaryItem item : itemList) {
                deleteSystemDictionaryItem(item);
            }
        }
        baseRepository.physicsDelete(systemDictionary);
    }

    @Override
    @Transactional
    public void deleteSystemDictionaryItem(SystemDictionaryItem item) {
        baseRepository.physicsDelete(item);
    }

    @Override
    public CutPage<SystemDictionary> querySystemDictionaryList(Integer status, String keyWord, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(100);
        hql.append("from SystemDictionary where 1 = 1 ");
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(keyWord)) {
            hql.append(" and (title like :keyword or sn like :keyWord )");
            map.put("keyword", "%" + keyWord + "%");
        }
        if (status != null) {
            hql.append(" and status = :status ");
            map.put("status", status);
        }
        return baseRepository.executeHql(hql, map, page, limit);
    }

    @Override
    public CutPage<SystemDictionaryItem> querySystemDictionaryItemList(Integer status, String orderBy, Long parentId) {
        StringBuilder hql = new StringBuilder(100);
        hql.append("from SystemDictionaryItem where 1 = 1");
        Map<String, Object> map = new HashMap<>();
        if (parentId != null) {
            hql.append(" and parentId = :parentId");
            map.put("parentId", parentId);
        }
        if (status != null) {
            hql.append(" and status = :status");
            map.put("status", status);
        }
        hql.append(" order by ");
        if (StringUtils.isNoneBlank(orderBy)) {
            switch (orderBy){
                case "value" : hql.append(" value desc, ");break;
            }
        }
        hql.append(" sequence");
        return baseRepository.executeHql(hql, map, 0, CutPage.MAX_COUNT);
    }

    @Override
    public void updateSystemDictionary(SystemDictionary systemDictionary) {
        this.checkSnUnique(systemDictionary);
        SystemDictionary sd = baseRepository.getObjById(SystemDictionary.class, systemDictionary.getId());
        if (sd != null) {
            sd.setSn(systemDictionary.getSn());
            sd.setTitle(systemDictionary.getTitle());
            sd.setValue(systemDictionary.getValue());
            baseRepository.updateObj(sd);
        }
    }

    @Override
    public void updateSystemDictionaryItem(SystemDictionaryItem item) {
        SystemDictionaryItem sdi = baseRepository.getObjById(SystemDictionaryItem.class, item.getId());
        if (sdi != null) {
            checkExpression(item.getExpression());
            sdi.setValue(item.getValue());
            sdi.setTitle(item.getTitle());
            sdi.setSequence(item.getSequence());
            sdi.setExpression(item.getExpression());
            sdi.setField(item.getField());
            baseRepository.updateObj(sdi);
        }
    }

    public void updateSystemDictionaryStatus(Long id, Integer status) {
        SystemDictionary sd = baseRepository.getObjById(SystemDictionary.class, id);
        if (sd != null && status != null) {
            sd.setStatus(status);
            baseRepository.updateObj(sd);
        }
    }

    public void updateSystemDictionaryItemStatus(Long id, Integer status) {
        SystemDictionaryItem sd = baseRepository.getObjById(SystemDictionaryItem.class, id);
        if (sd != null && status != null) {
            sd.setStatus(status);
            baseRepository.updateObj(sd);
        }
    }

    @Override
    public SystemDictionary getSystemDictionaryBySN(String sn) {
        return this.getSystemDictionaryBySN(sn, false);
    }

    @Override
    public SystemDictionary getSystemDictionaryBySN(String sn, boolean correlation) {
        String hql = "from SystemDictionary where sn = :sn and status = 1";
        Map<String, Object> map = new HashMap<>();
        map.put("sn", sn);
        SystemDictionary sd = baseRepository.executeHql(hql, map);
        if (correlation) {
            sd.setItems(this.querySystemDictionaryItemList(1, null, sd.getId()).getiData());
        }
        return sd;
    }

    @Override
    public SystemDictionaryItem getSystemDictionaryItemById(Integer status, Long parentId, Long id) {
        StringBuilder hql = new StringBuilder(200);
        hql.append("from SystemDictionaryItem where status = :status and id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("id", id);

        if (parentId != null) {
            hql.append("and parentId = :parentId ");
            map.put("parentId", parentId);
        }

        return baseRepository.executeHql(hql, map);
    }

    @Override
    public SystemDictionaryItem getSystemDictionaryItemById(Integer status, Long id) {
        return this.getSystemDictionaryItemById(status,null,id);
    }

    //校验表达式
    private void checkExpression(String expression) {
        try {
            // 检查表达式格式是否为json
            JSON.parse(expression);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "表达式格式不正确");
        }
    }

    //检查sn唯一
    private void checkSnUnique(SystemDictionary sd) {
        SystemDictionary systemDictionaryBySN = this.getSystemDictionaryBySN(sd.getSn());
        if (sd.getSn() != null && systemDictionaryBySN != null && !sd.getSn().equals(systemDictionaryBySN.getSn())) {
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR, "sn重复");
        }
    }
}
