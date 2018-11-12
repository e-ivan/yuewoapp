package com.war4.base;

import com.war4.repository.BaseRepository;
import com.war4.service.FileService;
import com.war4.service.SmsService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by hh on 2017.9.22 0022.
 */
public class BaseService implements ApplicationContextAware {
    protected ApplicationContext ac;
    @Autowired
    protected BaseRepository baseRepository;
    @Autowired
    protected SmsService smsService;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected MongoTemplate template;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
