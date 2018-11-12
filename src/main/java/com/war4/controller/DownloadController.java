package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.PropertiesConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 下载控制器
 * Created by hh on 2017.8.19 0019.
 */
@Controller
public class DownloadController  extends BaseController {
    @RequestMapping("download")
    public String download()throws Exception{
        return "redirect:" + PropertiesConfig.getAppDownloadPath();
    }
}
