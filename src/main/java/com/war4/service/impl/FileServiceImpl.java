package com.war4.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.base.PropertiesConfig;
import com.war4.enums.CommonDeleteFlag;
import com.war4.enums.CommonErrorResult;
import com.war4.enums.FilePurpose;
import com.war4.enums.FileTypeAndPath;
import com.war4.pojo.FileUpload;
import com.war4.pojo.MomentFile;
import com.war4.repository.BaseRepository;
import com.war4.service.FileService;
import com.war4.util.FileUploadUtils;
import com.war4.util.ImageCompressUtil;
import com.war4.vo.MomentFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by dly on 2016/8/12.
 */
@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private BaseRepository baseRepository;

    private final static boolean ZIP_FLAG = PropertiesConfig.isImgFileCompress(); //判断是否用压缩图

    @Override
    @Transactional
    public List<String> saveFile(FileTypeAndPath fileType, FilePurpose filePurpose, String fkPurposeId,MultipartFile... multipartFile) throws Exception {
        //如果找不到文档类型对应路径配置
        if (fileType == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "文档类型错误");
        }
        //如果用途不存在
        if (filePurpose == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "指定文件用途不存在");
        }
        //如果用途的绑定对象不存在
        /*Object belongToObj = baseRepository.getObjById(filePurposeObj.getBelongToClass(), fkPurposeId);
        if (belongToObj == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "指定绑定对象不存在");
        }*/
        List<String> list = new ArrayList<>();

        for (MultipartFile file : multipartFile) {
            String fileLogicLocation = FileUploadUtils.saveFileInLocation(file, fileType.getPath(), filePurpose, ZIP_FLAG);
            //保存文件信息到数据库
            insertFileInfoToDB(fileType.getType(), filePurpose.getCode(), fileLogicLocation, fkPurposeId);
            list.add(fileLogicLocation);
        }
        //返回数据
        return list;
    }

    @Override
    @Transactional
    public void insertFileInfoToDB(String fileType, String filePurpose, String fileLocation, String fkPurposeId) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setId(UUID.randomUUID().toString());
        fileUpload.setFileType(fileType);
        fileUpload.setPurpose(filePurpose);
        fileUpload.setLocation(fileLocation);
        fileUpload.setFkPurposeId(fkPurposeId);
        baseRepository.saveObj(fileUpload);
    }

    @Override
    public CutPage<FileUpload> getFilesByConditions(String fileType, String filePurpose, String fkPurposeId, Integer limit) {
        if (limit == null) {
            limit = CutPage.MAX_COUNT;
        }
        CutPage<FileUpload> cutPage = new CutPage(0, limit);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(baseRepository.getBaseHqlByClass(FileUpload.class))
                .append(" and fileType='").append(fileType).append("' ")
                .append(" and purpose='").append(filePurpose).append("' ")
                .append(" and fkPurposeId='").append(fkPurposeId).append("' ")
                .append(" order by createTime desc");

        return baseRepository.queryCutPageData(stringBuffer.toString(), cutPage);
    }

    @Override
    public List<FileUpload> getFilesByConditions(FileTypeAndPath fileType, FilePurpose filePurpose, String fkPurposeId) {
        String hql = "from FileUpload where delFlag = 0 and fileType = :fileType and purpose = :purpose and fkPurposeId = :fkPurposeId order by createTime desc";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("fileType",fileType.getType());
        paramMap.put("purpose",filePurpose.getCode());
        paramMap.put("fkPurposeId",fkPurposeId);
        return baseRepository.queryHqlResult(hql,paramMap,0,CutPage.MAX_COUNT);
    }

    //删除文件
    @Override
    @Transactional
    public void deleteFile(String fileId) {
        FileUpload fileUpload = baseRepository.getObjById(FileUpload.class, fileId);
        fileUpload.setDelFlag(CommonDeleteFlag.DELETED.getCode());
        baseRepository.updateObj(fileUpload);
    }

    @Override
    public String getCommonPhotoUrl(String fileType, String filePurpose, String fkPurposeId) {
        List<FileUpload> photoList = getFilesByConditions(fileType,
                filePurpose, fkPurposeId, 1).getiData();
        if (photoList != null && photoList.size() > 0) {
            return photoList.get(0).getLocation();
        }
        return null;
    }

    @Override
    @Transactional
    public List<MomentFileVO> saveMomentFiles(MultipartFile[] files, Long momentId, String userId) throws Exception {
        //获取文件类型对应路径
        String fileTypePath = FileTypeAndPath.getPathByType("image");
        String filePurpose = "imageMomentPhoto";
        //路径集合
        List<MomentFileVO> momentFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            //读取上传图片的信息
            InputStream inputStream = file.getInputStream();
            BufferedImage bi = ImageIO.read(inputStream);
            inputStream.close();
            String fileLogicLocation = FileUploadUtils.saveFileInLocation(file, fileTypePath, FilePurpose.MOMENT_PHOTO, ZIP_FLAG);
            //保存数据库数据
            MomentFile momentFile = new MomentFile();
            momentFile.setMomentId(momentId);
            momentFile.setUserId(userId);
            momentFile.setCreated(new Date());
            momentFile.setPath(fileLogicLocation);
            momentFile.setMime("image/jpeg");
            momentFile.setSizeH(bi.getHeight());
            momentFile.setSizeW(bi.getWidth());
            momentFile.setType("image");
            baseRepository.saveObj(momentFile);
            momentFiles.add(new MomentFileVO(fileLogicLocation,bi.getHeight(),bi.getWidth(),"image","image/jpeg"));
        }
        return momentFiles;
    }

    @Override
    public List<MomentFile> getMomentFilesById(Long momentId) {
        StringBuilder hql = new StringBuilder(200);
        hql.append(" from MomentFile where momentId = ").append(momentId);
        return baseRepository.queryCutPageData(hql.toString(), new CutPage<MomentFile>(0, Integer.MAX_VALUE)).getiData();
    }

    @Override
    @Transactional
    public List<String> saveFileForOtherPhoto(MultipartFile[] multipartFile, String fileType, String filePurpose, String fkPurposeId) throws IOException {
        //获取文件类型对应路径
        String fileTypePath = FileTypeAndPath.getPathByType(fileType);
        //如果找不到文档类型对应路径配置
        if (StringUtils.isEmpty(fileTypePath)) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "文档类型错误");
        }
        //如果用途不存在
        FilePurpose filePurposeObj = FilePurpose.getByCode(filePurpose);
        if (filePurposeObj == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "指定文件用途不存在");
        }
        List<String> list = new ArrayList<>();
        for (MultipartFile file : multipartFile) {
            //获取上传文件类型的扩展名
            String extensionName = FileUploadUtils.getExtensionName(file);
            //生成唯一文件标识，作为文件名
            String uuIdFileName = UUID.randomUUID().toString();
            //拼接文件完整存储URL
            String fileLocation = PropertiesConfig.getFileRoot() + fileTypePath + "/" + filePurpose + "/" + uuIdFileName + "." + extensionName;
            //拼接逻辑访问路径给前端请求
            String fileLogicLocation = PropertiesConfig.getFileLogicRoot() + fileTypePath + "/" + filePurpose + "/" + uuIdFileName + "." + extensionName;
            //创建文件
            File repositoryfile = new File(fileLocation);
            //如果目录不存在，则创建
            if (!repositoryfile.getParentFile().exists()) {
                repositoryfile.getParentFile().mkdirs();
            }
            //保存文件
            file.transferTo(repositoryfile);

            //保存文件信息到数据库
            insertFileInfoToDB(fileType, filePurpose, fileLogicLocation, fkPurposeId);
            list.add(fileLogicLocation);
        }
        //返回数据
        return list;
    }

    //压缩图片
    private void zipImage(MultipartFile file,String filePurpose,String fileLocation,int fileNum){

        if(filePurpose.equals(FilePurpose.IMAGE_USER_PHOTO_HEAD.getCode())){
            //上传头像压缩，后缀变成****_small
            ImageCompressUtil.zipImageFileWithPath(file, 120, 120, 1, "_small",fileLocation);
        }
        else if(filePurpose.equals(FilePurpose.MOMENT_PHOTO.getCode())){
            //上传相册压缩，后缀变成****_small
            ImageCompressUtil.zipCutMiddleImage(file, (float) 1, "_small",fileLocation,900,fileNum);
        }
    }



}
