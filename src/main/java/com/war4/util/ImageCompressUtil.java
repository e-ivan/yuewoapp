package com.war4.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.war4.base.PropertiesConfig;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片压缩工具
 * Created by hh on 2017.8.11 0011.
 */
public class ImageCompressUtil {
    /**
     * 直接指定压缩后的宽高：
     * @param oldFile 要进行压缩的文件全路径
     * @param width 压缩后的宽度
     * @param height 压缩后的高度
     * @param quality 压缩质量
     * @param smallIcon 文件名的小小后缀(注意，非文件后缀名称),入压缩文件名是yasuo.jpg,则压缩后文件名是yasuo(+smallIcon).jpg
     * @return 返回压缩后的文件的全路径
     */
    public static String zipImageFile(MultipartFile oldFile, Integer width, Integer height,
                                      float quality, String smallIcon) {
        if (oldFile == null) {
            return null;
        }
        String originalFilename = oldFile.getOriginalFilename();
        String newImage = null;
        try {
            /**对服务器上的临时文件进行处理 */
            Image srcFile = ImageIO.read(oldFile.getInputStream());
            /** 宽,高设定 */
            if (width == null){
                width = srcFile.getWidth(null);
            }
            if (height == null){
                height = srcFile.getHeight(null);
            }
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
            String filePrex = originalFilename.substring(0, originalFilename.indexOf('.'));
            /** 压缩后的文件名 */
            newImage = PropertiesConfig.getFileRoot() + "/" +  filePrex + smallIcon	+ originalFilename.substring(filePrex.length());
            /** 压缩之后临时存放位置 */
            FileOutputStream out = new FileOutputStream(newImage);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
            /** 压缩质量 */
            jep.setQuality(quality, true);
            encoder.encode(tag, jep);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newImage;
    }
    public static String zipImageFile(MultipartFile oldFile, float quality, String smallIcon){
        return zipImageFile(oldFile,null,null,quality,smallIcon);
    }

    /**
     * 保存文件到服务器临时路径(用于文件上传)
     * @param fileName
     * @param is
     * @return 文件全路径
     */
    public static String writeFile(String fileName, InputStream is) {
        if (fileName == null || fileName.trim().length() == 0) {
            return null;
        }
        try {
            /** 首先保存到临时文件 */
            FileOutputStream fos = new FileOutputStream(fileName);
            byte[] readBytes = new byte[512];// 缓冲大小
            int readed = 0;
            while ((readed = is.read(readBytes)) > 0) {
                fos.write(readBytes, 0, readed);
            }
            fos.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 等比例压缩算法：
     * 算法思想：根据压缩基数和压缩比来压缩原图，生产一张图片效果最接近原图的缩略图
     * @param srcFile 原文件
     * @param deskURL 缩略图地址
     * @param comBase 压缩基数
     * @param scale 压缩限制(宽/高)比例  一般用1：
     * 当scale>=1,缩略图height=comBase,width按原图宽高比例;若scale<1,缩略图width=comBase,height按原图宽高比例
     * @throws Exception
     * @author shenbin
     * @createTime 2014-12-16
     * @lastModifyTime 2014-12-16
     */
    public static void saveMinPhoto(MultipartFile srcFile, String deskURL, double comBase,
                                    double scale) throws Exception {
        InputStream inputStream = srcFile.getInputStream();
        Image src = ImageIO.read(inputStream);
        inputStream.close();
        int srcHeight = src.getHeight(null);
        int srcWidth = src.getWidth(null);
        int deskHeight = 0;// 缩略图高
        int deskWidth = 0;// 缩略图宽
        double srcScale = (double) srcHeight / srcWidth;
        /**缩略图宽高算法*/
        if ((double) srcHeight > comBase || (double) srcWidth > comBase) {
            if (srcScale >= scale || 1 / srcScale > scale) {
                if (srcScale >= scale) {
                    deskHeight = (int) comBase;
                    deskWidth = srcWidth * deskHeight / srcHeight;
                } else {
                    deskWidth = (int) comBase;
                    deskHeight = srcHeight * deskWidth / srcWidth;
                }
            } else {
                if ((double) srcHeight > comBase) {
                    deskHeight = (int) comBase;
                    deskWidth = srcWidth * deskHeight / srcHeight;
                } else {
                    deskWidth = (int) comBase;
                    deskHeight = srcHeight * deskWidth / srcWidth;
                }
            }
        } else {
            deskHeight = srcHeight;
            deskWidth = srcWidth;
        }
        BufferedImage tag = new BufferedImage(deskWidth, deskHeight, BufferedImage.TYPE_3BYTE_BGR);
        tag.getGraphics().drawImage(src, 0, 0, deskWidth, deskHeight, null); //绘制缩小后的图
        FileOutputStream deskImage = new FileOutputStream(deskURL); //输出到文件流
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(deskImage);
        encoder.encode(tag); //近JPEG编码
        deskImage.close();
    }

    public static BufferedImage  getNewImage(MultipartFile oldImage,double width,double height) throws IOException{
        /*srcURl 原图地址；deskURL 缩略图地址；comBase 压缩基数；scale 压缩限制(宽/高)比例*/

        ByteArrayInputStream bais = new ByteArrayInputStream(oldImage.getBytes());
        MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(bais);
        Image src = ImageIO.read(mciis);
        double srcHeight = src.getHeight(null);
        double srcWidth = src.getWidth(null);
        double deskHeight = 0;//缩略图高
        double deskWidth  = 0;//缩略图宽
        if (srcWidth>srcHeight) {

            if (srcWidth>width) {
                if (width/height>srcWidth/srcHeight) {
                    deskHeight = height;
                    deskWidth = srcWidth/(srcHeight/height);
                }
                else {
                    deskHeight = width/(srcWidth/srcHeight);
                    deskWidth = width;
                }
            }
            else {

                if (srcHeight>height) {
                    deskHeight = height;
                    deskWidth = srcWidth/(srcHeight/height);
                }else {
                    deskHeight=srcHeight;
                    deskWidth=srcWidth;
                }

            }

        }
        else if(srcHeight>srcWidth)
        {
            if (srcHeight>(height)) {
                if ((height)/width>srcHeight/srcWidth) {
                    deskHeight = srcHeight/(srcWidth/width);
                    deskWidth = width;
                }else {
                    deskHeight = height;
                    deskWidth = (height)/(srcHeight/srcWidth);
                }
            }
            else {
                if (srcWidth>width) {
                    deskHeight = srcHeight/(srcWidth/width);
                    deskWidth = width;
                }else {
                    deskHeight=srcHeight;
                    deskWidth=srcWidth;
                }

            }

        }
        else if (srcWidth==srcHeight) {

            if (width>=(height)&&srcHeight>(height)) {
                deskWidth=(height);
                deskHeight=(height);
            }
            else if (width<=(height)&&srcWidth>width) {
                deskWidth=width;
                deskHeight=width;
            }
            else  if (width==(height)&&srcWidth<width) {
                deskWidth=srcWidth;
                deskHeight=srcHeight;
            }
            else {
                deskHeight=srcHeight;
                deskWidth=srcWidth;
            }

        }
        BufferedImage tag = new BufferedImage((int)deskWidth,(int)deskHeight,
                BufferedImage.TYPE_3BYTE_BGR);
        tag.getGraphics().drawImage(src, 0, 0, (int)deskWidth, (int)deskHeight, null); //绘制缩小后的图
        return tag;
    }


    /**
     * 直接指定压缩后的宽高：
     * @param oldFile 要进行压缩的文件
     * @param width 压缩后的宽度
     * @param height 压缩后的高度
     * @param quality 压缩质量
     * @param smallIcon 文件名的小小后缀(注意，非文件后缀名称),入压缩文件名是yasuo.jpg,则压缩后文件名是yasuo(+smallIcon).jpg
     * @param  path 文件的全路径
     */

    public static void zipImageFileWithPath(MultipartFile oldFile, Integer width, Integer height,
                                      float quality, String smallIcon,String path) {

        String newImage;
        try {
            /**对服务器上的临时文件进行处理 */
            Image srcFile = ImageIO.read(oldFile.getInputStream());
            /** 宽,高设定 */
            if (width == null){
                width = srcFile.getWidth(null);
            }
            if (height == null){
                height = srcFile.getHeight(null);
            }
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
            String filePrex;//originalFilename.substring(0, originalFilename.indexOf('.'));
            /** 压缩后的文件名 */

            if (path.contains("jpg")){
                filePrex = path.replace(".jpg","");
                newImage = filePrex + smallIcon + ".jpg";
            }
            else {
                filePrex = path.replace(".png","");
                newImage = filePrex + smallIcon + ".png";
            }

            /** 压缩之后临时存放位置 */
            FileOutputStream out = new FileOutputStream(newImage);

            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
            /** 压缩质量 */
            if (oldFile.getSize()>120){
                jep.setQuality((float) 0.8, true);
            }
            else {
                jep.setQuality(1, true);
            }

            encoder.encode(tag, jep);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 截取中间部分 并压缩,图片大小有限制
     * @param oldFile 要进行压缩的文件
     * @param quality 压缩质量
     * @param smallIcon 文件名的小小后缀(注意，非文件后缀名称),入压缩文件名是yasuo.jpg,则压缩后文件名是yasuo(+smallIcon).jpg
     * @param  path 文件的全路径
     * @param  maxWidth 截取图片是正方形的宽度
     */
    //
    public static void zipCutMiddleImage(MultipartFile oldFile,float quality, String smallIcon,String path,int maxWidth,int fileNum)  {

        try {

            BufferedImage bufferedImage = ImageIO.read(oldFile.getInputStream());
            BufferedImage distin = null;
            // 返回源图片的宽度。
            int srcW = bufferedImage.getWidth();
            // 返回源图片的高度。
            int srcH = bufferedImage.getHeight();
            int x = 0, y = 0;

            int width;
            int wTemp = bufferedImage.getWidth();
            int hTemp = bufferedImage.getHeight();

            //如果上传图片数量大于1，则全部压缩截取中间，等于1则不用截取中间
            if (fileNum > 1){
                if (srcW>srcH){
                    if (srcW>maxWidth){
                        width = maxWidth;
                    }
                    else {
                        width = srcW;
                    }
                }
                else {
                    if (srcH>maxWidth){
                        width = maxWidth;
                    }
                    else {
                        width = srcH;
                    }
                }

                // 使截图区域居中
                x = srcW / 2 - width / 2;
                y = srcH / 2 - width / 2;
                srcW = srcW / 2 + width / 2;
                srcH = srcH / 2 + width / 2;

                wTemp = width;
                hTemp = width;
            }

            // 生成图片
            distin = new BufferedImage(wTemp, hTemp, BufferedImage.TYPE_INT_RGB);
            Graphics g = distin.getGraphics();
            g.drawImage(bufferedImage, 0, 0, wTemp, hTemp, x, y, srcW, srcH, null);

            String filePrex;
            String newImage;
            if (path.contains("png")){
                filePrex = path.replace(".png","");
                newImage = filePrex + smallIcon + ".png";
            }
            else {
                filePrex = path.replace(".jpg","");
                newImage = filePrex + smallIcon + ".jpg";
            }
            /** 压缩之后临时存放位置 */
            FileOutputStream out = new FileOutputStream(newImage);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(distin);
            /** 压缩质量 */
            if (oldFile.getSize()>120){
                jep.setQuality((float) 0.4, true);
            }
            else {
                jep.setQuality((float) 0.7, true);
            }

            encoder.encode(distin, jep);
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
