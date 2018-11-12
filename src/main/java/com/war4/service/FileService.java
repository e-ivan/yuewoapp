package com.war4.service;

import com.war4.base.CutPage;
import com.war4.enums.FilePurpose;
import com.war4.enums.FileTypeAndPath;
import com.war4.pojo.FileUpload;
import com.war4.pojo.MomentFile;
import com.war4.vo.MomentFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by dly on 2016/8/12.
 */
public interface FileService {
    //存储文件到文件系统
    List<String> saveFile(FileTypeAndPath fileType, FilePurpose filePurpose,String fkPurposeId,MultipartFile... multipartFile) throws Exception;
    //存储文件信息到数据库
    void insertFileInfoToDB(String fileType,String filePurpose,String fileLocation,String fkPurposeId);
    //查询文件
    CutPage<FileUpload> getFilesByConditions(String fileType,String filePurpose,String fkPurposeId,Integer limit);
    List<FileUpload> getFilesByConditions(FileTypeAndPath fileType, FilePurpose filePurpose, String fkPurposeId);
    //删除文件
    void deleteFile(String fileId);

    String getCommonPhotoUrl(String fileType,String filePurpose,String fkPurposeId);

    //保存朋友圈图片集
    List<MomentFileVO> saveMomentFiles(MultipartFile[] files, Long momentId, String userId) throws Exception;

    //获取朋友圈里的相册
    List<MomentFile> getMomentFilesById(Long momentId);

    List<String> saveFileForOtherPhoto(MultipartFile[] multipartFile, String fileType, String filePurpose, String fkPurposeId) throws IOException;

}
