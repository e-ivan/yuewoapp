package com.war4.vo.film;

import com.war4.base.BaseVO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 电影信息
 * Created by hh on 2017.8.17 0017.
 */
@Document(collection = "KOU_CINEMA_MOVIE")
@Getter
@Setter
public class MovieVO extends BaseVO{
    private String actor;   //演员
    private String director;//导演
    private Boolean has2D;
    private Boolean has3D;
    private Boolean hasImax;
    private Integer hot;    //热度
    private String intro;
    private Long movieId;
    private Integer movieLength;
    private String movieName;
    private String movieType;
    private String pathHorizonH;
    private String pathVerticalS;
    private String publishTime;
    private Double score;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date publishTimeDate;
    private Boolean expired;
    private String source;      //来源
    private Trailer trailer;
    private String rawMovieName;//原始电影名称
    private String cineMovieNum;

    public Date getPublishTimeDate(){
        try {
            return DateUtils.parseDateStrictly(this.publishTime, "yyyy-MM-dd");
        } catch (Exception e) {
            return new Date();
        }
    }

    public boolean isPublish(){
        return new Date().after(this.getPublishTimeDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieVO vo = (MovieVO) o;

        return movieId.equals(vo.movieId);
    }

    @Override
    public int hashCode() {
        return movieId.hashCode();
    }

    @Getter
    @Setter
    public class Trailer {
        private String trailerCover;//预告片遮盖图
        private String trailerPath;//预告片地址
    }
}
