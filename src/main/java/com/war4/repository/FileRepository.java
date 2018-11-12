package com.war4.repository;

import com.war4.pojo.FileUpload;

import java.util.List;

/**
 * Created by dly on 2016/8/25.
 */
public interface FileRepository {
    //查询文件
    List<FileUpload> getFilesByConditions(String fileType, String filePurpose, String fkPurposeId, Integer limit);
    //插入文件信息
    void insertFile(FileUpload fileUpload);
}
