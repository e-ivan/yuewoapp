package com.war4.repository;

/**
 * mongodb通用仓库
 * Created by hh on 2017.9.22 0022.
 */
public interface BaseMongoRepository {

    <T> void saveObj(T obj);

    <T> void upsertObj(T obj);

}
