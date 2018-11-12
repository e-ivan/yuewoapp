package com.war4.util;

import com.war4.base.PropertiesConfig;
import com.war4.enums.FilePurpose;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Pattern;

public interface FileUploadUtils {
    String SMALL = "_small";
    String X_SMALL = "_xSmall";
    double X_SMALL_MIN_HEIGHT = 70;

    static String saveFileInLocation(MultipartFile file, String fileTypeDir, FilePurpose filePurpose, boolean ImgCompress) throws Exception {
        //获取上传文件类型的扩展名
        String extensionName = FileUploadUtils.getExtensionName(file);
        //生成唯一文件标识，作为文件名
        String uuIdFileName = UUID.randomUUID().toString();
        String filePre = fileTypeDir + "/" + filePurpose.getCode() + "/" + uuIdFileName;
        Pattern pattern = Pattern.compile("^/.*");
        if (!pattern.matcher(filePre).matches()) {
            filePre = "/" + filePre;
        }
        String filePath = filePre + "." + extensionName;
        //拼接文件完整存储URL
        String fileLocation = PropertiesConfig.getFileRoot() + filePath;
        //拼接逻辑访问路径给前端请求
        String fileLogicLocation = PropertiesConfig.getFileLogicRoot() + filePath;
        //创建文件
        File repositoryfile = new File(fileLocation);
        //如果目录不存在，则创建
        if (!repositoryfile.getParentFile().exists()) {
            repositoryfile.getParentFile().mkdirs();
        }
        //保存文件
        InputStream inputStream = file.getInputStream();
        FileUtils.copyInputStreamToFile(inputStream, repositoryfile);
        inputStream.close();
        Double smallHeight = filePurpose.getSmallHeight();
        //压缩图片
        if (ImgCompress && smallHeight != null) {
            String smallPath = filePre + SMALL + "." + extensionName;
            String smallLocation = PropertiesConfig.getFileRoot() + smallPath;
            Image src = ImageIO.read(repositoryfile);
            int srcHeight = src.getHeight(null);
            int srcWidth = src.getWidth(null);
            int scale = 1;
            if (srcHeight > srcWidth) {
                scale = -1;
            }
            ImageCompressUtil.saveMinPhoto(file, smallLocation, smallHeight, scale);
            fileLogicLocation = PropertiesConfig.getFileLogicRoot() + smallPath;
            //如果是头像，超小比例
            if (FilePurpose.IMAGE_USER_PHOTO_HEAD.equals(filePurpose)) {
                String xSmallPath = filePre + X_SMALL + "." + extensionName;
                String xSmallLocation = PropertiesConfig.getFileRoot() + xSmallPath;
                ImageCompressUtil.saveMinPhoto(file, xSmallLocation, X_SMALL_MIN_HEIGHT, scale);
            }
        }
        return fileLogicLocation;
    }

    //获取上传文件类型的扩展名,先得到.的位置，再截取从.的下一个位置到文件的最后，最后得到扩展名
    static String getExtensionName(MultipartFile file) {
        String fullName = file.getOriginalFilename();
        return getSuffix(fullName);
    }

    static String getSuffix(String str) {
        return str.substring(str.lastIndexOf(".") + 1);
    }

    static String getImageOfSize(String src, String size) {
        if (StringUtils.isNoneBlank(src)) {
            //查看是否为指定结尾
            Pattern pattern = Pattern.compile(".*_.*mall\\..*");
            if (pattern.matcher(src).matches()) {
                return StringUtils.replacePattern(src, "_.*mall", size);
            }
        }
        return src;
    }

    static String getSmallImage(String src) {
        return getImageOfSize(src, SMALL);
    }

    static String getXSmallImage(String src) {
        return getImageOfSize(src, X_SMALL);
    }

    //去除图片后缀
    static String removeImageSuffix(String src) {
        if (StringUtils.isNoneBlank(src)) {
            return StringUtils.replacePattern(src, "_.*mall", "");
        }
        return src;
    }

}
