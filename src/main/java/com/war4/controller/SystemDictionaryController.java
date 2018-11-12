package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.enums.CommonWhether;
import com.war4.pojo.SystemDictionary;
import com.war4.pojo.SystemDictionaryItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 数据字典控制器
 * Created by hh on 2017.6.24 0024.
 */
@Controller
@RequestMapping(value = "systemDictionary")
public class SystemDictionaryController  extends BaseController {

    @RequestMapping(value = "systemDictionaryList")
    @ResponseBody
    public Response systemDictionaryList(Integer status, String keyWord, Integer page, Integer limit) throws Exception {
        return new ObjectResponse<>(systemDictionaryService.querySystemDictionaryList(status, keyWord, page, limit));
    }

    @RequestMapping(value = "systemDictionaryItemList")
    @ResponseBody
    public Response systemDictionaryItemList(Integer status,String orderBy, Long parentId) throws Exception {
        return new ObjectResponse<>(systemDictionaryService.querySystemDictionaryItemList(status,orderBy, parentId));
    }

    @RequestMapping(value = "queryEnrollDictionaryItemList")
    @ResponseBody
    public Response queryEnrollDictionaryItemList() throws Exception {
        SystemDictionary sd = systemDictionaryService.getSystemDictionaryBySN("enrollFormQuestions", true);
        return new ObjectResponse<>(sd != null && sd.getItems().size() > 0 ? sd.getItems().get(0) : null);
    }

    @RequestMapping(value = "addSystemDictionary")
    @ResponseBody
    public Response addSystemDictionary(SystemDictionary systemDictionary) throws Exception {
        systemDictionaryService.addSystemDictionary(systemDictionary);
        return new Response();
    }

    @RequestMapping(value = "addSystemDictionaryItem")
    @ResponseBody
    public Response addSystemDictionaryItem(SystemDictionaryItem item) throws Exception {
        systemDictionaryService.addSystemDictionaryItem(item);
        return new Response(item.getParentId().toString());
    }

    @RequestMapping(value = "deleteSystemDictionary")
    @ResponseBody
    public Response deleteSystemDictionary(SystemDictionary systemDictionary) throws Exception {
        systemDictionaryService.deleteSystemDictionary(systemDictionary);
        return new Response();
    }

    @RequestMapping(value = "deleteSystemDictionaryItem")
    @ResponseBody
    public Response deleteSystemDictionaryItem(SystemDictionaryItem item) throws Exception {
        systemDictionaryService.deleteSystemDictionaryItem(item);
        return new Response();
    }

    @RequestMapping(value = "updateSystemDictionary")
    @ResponseBody
    public Response updateSystemDictionary(SystemDictionary systemDictionary) throws Exception {
        systemDictionaryService.updateSystemDictionary(systemDictionary);
        return new Response();
    }

    @RequestMapping(value = "updateSystemDictionaryItem")
    @ResponseBody
    public Response updateSystemDictionaryItem(SystemDictionaryItem item) throws Exception {
        systemDictionaryService.updateSystemDictionaryItem(item);
        return new Response();
    }
    //更新数据字典状态
    @RequestMapping(value = "updateSystemDictionaryStatus")
    @ResponseBody
    public Response updateSystemDictionaryStatus(Long id,Integer status) throws Exception {
        systemDictionaryService.updateSystemDictionaryStatus(id, status);
        return new Response();
    }
    //更新数据字典明细状态
    @RequestMapping(value = "updateSystemDictionaryItemStatus")
    @ResponseBody
    public Response updateSystemDictionaryItemStatus(Long id,Integer status) throws Exception {
        systemDictionaryService.updateSystemDictionaryItemStatus(id, status);
        return new Response();
    }

    //根据sn获取数据字典
    @RequestMapping(value = "getSystemDictionaryBySn")
    @ResponseBody
    public Response getSystemDictionaryBySn(String sn) throws Exception {
        return new ObjectResponse<>(systemDictionaryService.getSystemDictionaryBySN(sn,true));
    }

    //根据数据字典分类sn获取明细
    @RequestMapping(value = "queryItemBySN")
    @ResponseBody
    public Response queryItemBySN(String sn) throws Exception {
        SystemDictionary dictionary = systemDictionaryService.getSystemDictionaryBySN(sn);
        if (dictionary == null) {
            return new ObjectResponse<>("");
        }
        return new ObjectResponse<>(systemDictionaryService.querySystemDictionaryItemList(CommonWhether.TRUE.getCode(), null, dictionary.getId()).getiData());
    }

}
