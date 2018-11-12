package com.war4.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by hh on 2017.9.1 0001.
 */
@Getter
@AllArgsConstructor
public enum MediaType {
    观摩影片("数字","2D","观摩影片"),
    普通("数字","2D","普通"),
    普通立体("数字","3D","普通立体"),
    IMAX("数字","2D","IMAX"),
    IMAX立体("数字","3D","IMAX立体"),
    胶片("胶片","2D","胶片"),
    其他特种影片("数字","2D","其他特种电影"),
    其他("数字","2D","其他"),
    巨幕("数字","2D","中国巨幕"),
    巨幕立体("数字","3D","巨幕立体"),
    四D("数字","4D","普通");

    private String movieFormat;
    private String movieDimensional;
    private String movieSize;

    public static String getMovieDimensionalByMovieSize(String movieSize){
        for (MediaType mediaType : MediaType.values()) {
            if (mediaType.getMovieSize().equals(movieSize)) {
                return mediaType.getMovieDimensional();
            }
        }
        return null;
    }

}
