package com.war4.listener.event;

import com.war4.vo.film.FilmOrderVO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 电影订单事件
 * Created by hh on 2017.8.15 0015.
 */
@Getter
public class FilmOrderEvent extends ApplicationEvent {
    private FilmOrderVO filmOrderVO;

    public FilmOrderEvent(FilmOrderVO filmOrderVO) {
        super(filmOrderVO);
        this.filmOrderVO = filmOrderVO;
    }
}
