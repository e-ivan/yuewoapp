package com.war4.controller;

import com.war4.base.BaseController;
import com.war4.base.ObjectResponse;
import com.war4.base.Response;
import com.war4.enums.FilePurpose;
import com.war4.enums.FileTypeAndPath;
import com.war4.util.ImageCompressUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by dly on 2016/7/22.
 */
@Controller
@RequestMapping(value = "file")
public class FileController extends BaseController {

    //base64方式
//    @RequestMapping(value = "uploadImage",method = RequestMethod.POST)
//    public Map uploadImage(String formFile,String imageType) throws Exception{
//        String savePath =imagePath + File.separator + imageType + File.separator;
//        byte[] bytes = new BASE64Decoder().decodeBuffer(formFile);
//        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//        BufferedImage bi = ImageIO.read(bais);
//        String fileName = "aaaa" + ".jpg";
//        File file = new File(savePath);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        ImageIO.write(bi, "jpg", file);
//        return null;
//    }

    /**
     * MultipartFile方式
     */
    @RequestMapping(value = "uploadFile", method = RequestMethod.POST)
    public
    @ResponseBody
    Response uploadFile(
            @RequestParam(value = "uploadFile", required = true) MultipartFile[] multipartFile,
            @RequestParam(value = "fileType", required = true) String fileType,
            @RequestParam(value = "filePurpose", required = true) String filePurpose,
            @RequestParam(value = "fkPurposeId", required = true) String fkPurposeId) throws Exception {
        return new ObjectResponse<>(fileService.saveFile(FileTypeAndPath.getByType(fileType), FilePurpose.getByCode(filePurpose) , fkPurposeId,multipartFile));
    }

    /**
     * MultipartFile方式
     */
    @RequestMapping(value = "uploadFileForMoviePhoto", method = RequestMethod.POST)
    public
    @ResponseBody
    Response uploadFileForMoviePhoto(
            @RequestParam(value = "uploadFile", required = true) MultipartFile[] multipartFile,
            @RequestParam(value = "fileType", required = true) String fileType,
            @RequestParam(value = "filePurpose", required = true) String filePurpose,
            @RequestParam(value = "fkPurposeId", required = true) String fkPurposeId) throws IOException {
        return new ObjectResponse<>(fileService.saveFileForOtherPhoto(multipartFile, fileType, filePurpose, fkPurposeId));
    }

    /**
     * MultipartFile方式
     */
    @RequestMapping(value = "uploadFileForCinemaPhoto", method = RequestMethod.POST)
    public
    @ResponseBody
    Response uploadFileForCinemaPhoto(
            @RequestParam(value = "uploadFile", required = true) MultipartFile[] multipartFile,
            @RequestParam(value = "fileType", required = true) String fileType,
            @RequestParam(value = "filePurpose", required = true) String filePurpose,
            @RequestParam(value = "fkPurposeId", required = true) String fkPurposeId) throws IOException {
        return new ObjectResponse<>(fileService.saveFileForOtherPhoto(multipartFile, fileType, filePurpose, fkPurposeId));
    }


    @RequestMapping(value = "deleteFile", method = RequestMethod.POST)
    public
    @ResponseBody
    Response deleteFile(@RequestParam(value = "fileId", required = true) String fileId) throws IOException {
        fileService.deleteFile(fileId);
        return new Response("文件删除成功");
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public
    @ResponseBody
    Response upload(MultipartFile file,double comBase) throws IOException {
        try {
            String small = ImageCompressUtil.zipImageFile(file, (float) comBase, "_small");
            return new ObjectResponse<>(small);
        } catch (Exception e) {
            e.printStackTrace();
        return new Response("上传失败");
        }
    }

}
