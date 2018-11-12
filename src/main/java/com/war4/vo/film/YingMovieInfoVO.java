package com.war4.vo.film;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 影片信息
 * Created by hh on 2017.8.23 0023.
 */
@Document(collection = "MOVIE_INFO")
@Getter
@Setter
public class YingMovieInfoVO extends BaseVO{
    private Long cineMovieId;
    private String cinemaId;
    private String cineMovieNum;
    private String cineMovieNumToEquals;
    private String movieName;
    private String movieLanguage;
    private String movieSubtitle;
    private String movieFormat;
    private String movieSize;
    private String movieDimensional;//影片放映类型
    private Date joinStartTime;
    private Date joinEndTime;
    private Integer movieLength;
    private Date created;
    private Long kouMovieId;    //抠电影对应的movieId


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YingMovieInfoVO that = (YingMovieInfoVO) o;

        return cineMovieNum != null ? cineMovieNum.equals(that.cineMovieNum) : that.cineMovieNum == null;
    }

    @Override
    public int hashCode() {
        return cineMovieNum != null ? cineMovieNum.hashCode() : 0;
    }

}
