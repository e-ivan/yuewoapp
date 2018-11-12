package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.SystemDictionary;
import com.war4.pojo.SystemDictionaryItem;

/**
 * 数据字典服务
 * Created by hh on 2017.6.24 0024.
 */
public interface SystemDictionaryService {
    void addSystemDictionary(SystemDictionary systemDictionary);
    void addSystemDictionaryItem(SystemDictionaryItem item);
    void deleteSystemDictionary(SystemDictionary systemDictionary);
    void deleteSystemDictionaryItem(SystemDictionaryItem item);

    CutPage<SystemDictionary> querySystemDictionaryList(Integer status,String keyWord,Integer page,Integer limit);
    CutPage<SystemDictionaryItem> querySystemDictionaryItemList(Integer status, String orderBy, Long parentId);

    void updateSystemDictionary(SystemDictionary systemDictionary);

    void updateSystemDictionaryItem(SystemDictionaryItem item);

    void updateSystemDictionaryStatus(Long id,Integer status);
    void updateSystemDictionaryItemStatus(Long id,Integer status);

    //根据sn获取数据字典
    SystemDictionary getSystemDictionaryBySN(String sn);
    SystemDictionary getSystemDictionaryBySN(String sn,boolean correlation);
    //根据id查询数据字典明细
    SystemDictionaryItem getSystemDictionaryItemById(Integer status,Long parentId,Long id);
    SystemDictionaryItem getSystemDictionaryItemById(Integer status,Long id);
}
