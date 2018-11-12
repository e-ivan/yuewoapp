package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by dly on 2016/8/12.
 */
@Getter
@AllArgsConstructor
public enum FileTypeAndPath {
    IMAGE_TYPE_PATH("image", "/image"),
    APP_TYPE_PATH("app", "/app"),
    VIDEO_TYPE_PATH("video", "/video"),
    VOICE_TYPE_PATH("voice", "/voice");

    private String type;
    private String path;

    public static String getPathByType(String type){
        for(FileTypeAndPath fileTypeAndPath: FileTypeAndPath.values()){
            if (fileTypeAndPath.getType().equals(type)){
                return fileTypeAndPath.getPath();
            }
        }
        return null;
    }
    public static FileTypeAndPath getByType(String type){
        for(FileTypeAndPath fileTypeAndPath: FileTypeAndPath.values()){
            if (fileTypeAndPath.getType().equals(type)){
                return fileTypeAndPath;
            }
        }
        return null;
    }
}
