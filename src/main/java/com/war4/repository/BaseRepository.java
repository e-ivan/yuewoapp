package com.war4.repository;

import com.war4.base.CutPage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by dly on 2016/9/13.
 */
public interface BaseRepository {
    //基本增删(物理删除)改查
    <T> void saveObj(T obj);
    <T> void updateObj(T obj);
    <T,K> T getObjById(Class<T> clazzT,K id);
    //根据传递的id获取map,可传递指定索引（多个值返回最后一个）
    <T,E> Map<E,T> queryObjMap(Class<T> clz, Collection<E> ids,String identify);
    <T,E> Map<E,T> queryObjMap(Class<T> clz, Collection<E> ids);
    //获取解析后对象id
    Serializable objectIdOfType(Class clz,Serializable id);
    <T> void physicsDelete(T obj);

    //逻辑删除
    <T> void logicDelete(T needLogicDelObj);
    //根据类名获取基本hql
    @Deprecated
    String getBaseHqlByClass(Class T);
    //两表联合查询
//    public <T> String getJoinTableHql(Class classT,Class classInner,String propertyT,String propertyInner);
    //查询hql结果总数
    @Deprecated
    Long getTotalCount(String sql);
    //查询sql结果总数
    @Deprecated
    Long getSQLTotalCount(String headerSql,String sql);

    //查询分页结果
    @Deprecated
    <T> CutPage<T> queryCutPageData(String hql,CutPage<T> cutPage);
    @Deprecated
    <T> CutPage<T> querySQLCutPageData(String headerSql,String headerCountSql,String sql,CutPage<T> cutPage);

    //查询单个结果
    @Deprecated
    <T> T queryUniqueData(CharSequence hql);
    /**
     * 执行自定义hql语句
     * @param hql
     * @param paramMap 占位符参数
     */
    <T> T executeHql(CharSequence hql,Map<String,Object> paramMap);

    /**
     * 执行hql查询结果集
     * @param hql
     */
    <T> CutPage<T> executeHql(CharSequence hql,Map<String,Object> paramMap,Integer page,Integer limit);

    <T> List<T> queryHqlResult(CharSequence hql, Map<String,Object> paramMap, Integer page, Integer limit);


    /**
     * 执行sql查询结果集
     */
    <T> CutPage<T> executeSql(CharSequence sql,Map<String,Object> paramMap,Class<T> entity,Integer page,Integer limit);

    <T> List<T> querySqlResult(CharSequence sql, Map<String,Object> paramMap,Class<T> entity, Integer page, Integer limit);
    /**
     * 执行sql获取单条结果
     * @param sql
     * @param paramMap
     */
    <T> T executeSql(CharSequence sql,Map<String,Object> paramMap,Class<T> entity);

    Map<String,Object> paramMap();

}
