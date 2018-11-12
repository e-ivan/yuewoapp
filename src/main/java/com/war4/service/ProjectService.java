package com.war4.service;

import com.war4.base.CutPage;
import com.war4.pojo.Project;

import java.util.Properties;

/**
 * Created by Administrator on 2016/12/22.
 */
public interface ProjectService {
    Project saveProject(Project project);
    Project queryProjectById(String projectId);
    void deleteProectById(String projectId);
    CutPage<Project> queryProjectList(Integer page,Integer limit);
}
