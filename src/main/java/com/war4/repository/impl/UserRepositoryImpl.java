package com.war4.repository.impl;

import com.war4.repository.UserRepository;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * Created by dly on 2016/7/12.
 */
@Repository
@SuppressWarnings("unchecked")
public class UserRepositoryImpl extends HibernateDaoSupport implements UserRepository {


}
