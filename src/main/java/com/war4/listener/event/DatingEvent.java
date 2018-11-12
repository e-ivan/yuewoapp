package com.war4.listener.event;

import com.war4.pojo.DatingFilm;
import org.springframework.context.ApplicationEvent;

/**
 * Created by hh on 2017.8.30 0030.
 */
public class DatingEvent extends ApplicationEvent {

    private DatingFilm datingFilm;

    public DatingEvent(DatingFilm datingFilm) {
        super(datingFilm);
        this.datingFilm = datingFilm;
    }

    public DatingFilm getDatingFilm() {
        return datingFilm;
    }
}
