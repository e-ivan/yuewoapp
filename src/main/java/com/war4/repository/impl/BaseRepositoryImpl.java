package com.war4.repository.impl;


import com.war4.base.BusinessException;
import com.war4.base.CutPage;
import com.war4.enums.CommonDeleteFlag;
import com.war4.enums.CommonErrorResult;
import com.war4.repository.BaseRepository;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.StaleObjectStateException;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dly on 2016/8/29.
 */
@Repository
@SuppressWarnings("unchecked")
public class BaseRepositoryImpl extends HibernateDaoSupport implements BaseRepository {

    @Override
    public <T> void saveObj(T obj) {
        try {
            if (obj instanceof Collection) {
                for (Object o : ((Collection) obj)) {
                    getHibernateTemplate().save(o);
                }
            }else {
                getHibernateTemplate().save(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public <T> void updateObj(T obj) {
        try {
            getHibernateTemplate().update(obj);
        }catch (StaleObjectStateException ex){
            //乐观锁失败
            throw new BusinessException(CommonErrorResult.BUSINESS_ERROR,"网络异常,请重新操作");
        }
    }

    @Override
    public <T, K> T getObjById(Class<T> clazzT, K id) {
        //参数检查主键和所属类缺一不可
        if (clazzT == null || id == null) {
            return null;
        }
        Serializable idOfType = this.objectIdOfType(clazzT, (Serializable) id);
        if (idOfType == null) {
            return null;
        }
        return getHibernateTemplate().get(clazzT, idOfType);
    }

    @Override
    public <T, E> Map<E, T> queryObjMap(Class<T> clz, Collection<E> ids,String identify) {
        String idName = getObjectIdName(clz);
        if (identify != null) {
            idName = identify;
        }
        if (ids == null || ids.isEmpty()|| idName == null) {
            return new HashMap<>();
        }
        //获取id主键名称
        //获取对象的映射关联
        String hql = "from " + clz.getName() + " where " + idName + " in :ids";
        Map<String, Object> map = this.paramMap();
        map.put("ids",ids);
        List<T> result = this.queryHqlResult(hql, map, 0, CutPage.MAX_COUNT);
        //获取属性的读方法
        Method idReadMethod = getReadMethodByPropertyName(clz, idName);
        Map collect = result.stream().collect(Collectors.toMap(o -> {
            try {
                return idReadMethod.invoke(o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        },o -> o));
        return collect;
    }

    @Override
    public <T, E> Map<E, T> queryObjMap(Class<T> clz, Collection<E> ids) {
        return this.queryObjMap(clz, ids,null);
    }

    private static String getObjectIdName(Class clz){
        for (Field f : clz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)) {
                return f.getName();
            }
        }
        return null;
    }

    //根据属性名获取读方法
    private static Method getReadMethodByPropertyName(Class clz,String propertyName){
        PropertyDescriptor[] pds = new PropertyDescriptor[0];
        try {
            pds = Introspector.getBeanInfo(clz, Object.class).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        Optional<PropertyDescriptor> first = Stream.of(pds).filter(p -> StringUtils.equals(p.getName(), propertyName)).findFirst();
        return first.map(PropertyDescriptor::getReadMethod).orElse(null);
    }


    @Override
    public Serializable objectIdOfType(Class clz, Serializable id) {
        boolean isStr = id.getClass().equals(String.class);
        if (isStr && StringUtils.isBlank((String)id)) {
            return null;
        }
        Class type = Object.class;
        for (Field f : clz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)) {
                type = f.getType();
                break;
            }
        }
        Serializable typeOfId = null;
        if (type.equals(String.class)){
            typeOfId = isStr ? (String) id : id.toString();
        }else if (type.equals(Long.class)){
            typeOfId = isStr ? Long.parseLong((String) id) : (Long) id;
        }else if (type.equals(Integer.class)){
            typeOfId = isStr ? Integer.parseInt((String) id) : (Integer) id;
        }
        return typeOfId;
    }

    @Override
    public <T> void physicsDelete(T obj) {
        getHibernateTemplate().delete(obj);
    }

    @Override
    public <T> void logicDelete(T needLogicDelObj) {
        try {
            //为删除标记属性创建set方法
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor("delFlag", needLogicDelObj.getClass());//为当前属性创建属性描述对象
            //set删除标记为已删除
            propertyDescriptor.getWriteMethod().invoke(needLogicDelObj, CommonDeleteFlag.DELETED.getCode());
            //更新
            updateObj(needLogicDelObj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "逻辑删除出错");
        }
    }


    //获取基本SQL
    @Override
    @Deprecated
    public String getBaseHqlByClass(Class clazz) {
        if (clazz == null) {
            throw new BusinessException(CommonErrorResult.SECRET_FAIL, "获取基本HQL出错，参数不全");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("from ").append(clazz.getSimpleName()).append(" where delFlag=").append(CommonDeleteFlag.NOT_DELETED.getCode()).append(" ");
        String baseHql = sb.toString();
        return baseHql;
    }

    //查询sql记录总数
    @Override
    @Deprecated
    public Long getTotalCount(String sql) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) ").append(sql);
        return getHibernateTemplate().execute(session -> {
            Query query = session.createQuery(sb.toString());
            return (Long) query.uniqueResult();
        });
    }

    @Override
    @Deprecated
    public Long getSQLTotalCount(String headerSql, String sql) {
        StringBuilder sb = new StringBuilder();
        sb.append(headerSql).append(sql);
        return getHibernateTemplate().execute(session -> {
            Query query = session.createSQLQuery(sb.toString());
            return Long.valueOf(query.uniqueResult().toString());
        });
    }


    //查询分页数据
    @Override
    @Deprecated
    public <T> CutPage<T> queryCutPageData(String hql, CutPage<T> cutPage) {
        List<T> idata = (List<T>) getHibernateTemplate().execute((HibernateCallback) session -> {
            Query query = session.createQuery(hql);
            query.setFirstResult(cutPage.getOffset());
            query.setMaxResults(cutPage.getLimit());
            return query.list();
        });
        Long totalCount = this.getTotalCount(hql);
        cutPage.setTotalCount(totalCount == null ? 0L : totalCount);//为null则返回0
        cutPage.setiData(idata == null ? Collections.EMPTY_LIST : idata);//为null则返回空集
        return cutPage;
    }

    //查询分页数据
    @Override
    @Deprecated
    public <T> CutPage<T> querySQLCutPageData(String headerSql, String headerCountSql, String sql, CutPage<T> cutPage) {
        List<T> idata = (List<T>) getHibernateTemplate().execute((HibernateCallback) session -> {
            Query query = session.createSQLQuery(headerSql + sql);
            query.setFirstResult(cutPage.getOffset());
            query.setMaxResults(cutPage.getLimit());
            return query.list();
        });
        Long sqlTotalCount = this.getSQLTotalCount(headerCountSql, sql);
        cutPage.setTotalCount(sqlTotalCount == null ? 0L : sqlTotalCount);
        cutPage.setiData(idata == null ? Collections.EMPTY_LIST : idata);
        return cutPage;
    }

    //查询单个结果
    @Deprecated
    public <T> T queryUniqueData(CharSequence hql) {
        T result = (T) getHibernateTemplate().execute(session -> {
            Query query = session.createQuery(hql.toString());
            query.setMaxResults(1);
            return query.uniqueResult();
        });
        return result;
    }

    @Override
    public <T> T executeHql(CharSequence hql, Map<String, Object> paramMap) {
        return (T) getHibernateTemplate().execute(session -> {
            Query q = session.createQuery(hql.toString());
            settingParameters(q,paramMap);
            if (judgeNotSelectQl(hql)) {
                return q.executeUpdate();
            }
            q.setMaxResults(1);
            return q.uniqueResult();
        });
    }

    /**
     * 判断语句不是查询语句
     */
    private static boolean judgeNotSelectQl(CharSequence ql){
        String hqlStr = ql.toString();
        String form = hqlStr.trim().substring(0, 6);
        return form.toLowerCase().contains("update") || form.toLowerCase().contains("delete") || form.toLowerCase().contains("insert");
    }

    @Override
    public <T> CutPage<T> executeHql(CharSequence hql, Map<String, Object> paramMap, Integer page, Integer limit) {
        CutPage<T> cutPage = new CutPage<>(page, limit);
        cutPage.setTotalCount(executeHql(generatePagingStatement(hql), paramMap));
        cutPage.setiData(this.queryHqlResult(hql,paramMap,page,limit));
        return cutPage;
    }

    @Override
    public <T> List<T> queryHqlResult(CharSequence hql, Map<String, Object> paramMap, Integer page, Integer limit) {
        return  (List<T>) getHibernateTemplate().execute((HibernateCallback) session -> {
            Query q = session.createQuery(hql.toString());
            settingParameters(q,paramMap);
            q.setFirstResult((page == null ? 0 : page) * (limit == null ? 10 : limit));
            q.setMaxResults((limit == null ? 10 : limit));
            return q.list();
        });
    }

    @Override
    public <T> CutPage<T> executeSql(CharSequence sql, Map<String, Object> paramMap, Class<T> entity, Integer page, Integer limit) {
        CutPage<T> cutPage = new CutPage<>(page, limit);
        cutPage.setTotalCount(this.executeSql(generatePagingStatement(sql), paramMap,null));
        cutPage.setiData(this.querySqlResult(sql, paramMap, entity, page, limit));
        return cutPage;
    }

    @Override
    public <T> List<T> querySqlResult(CharSequence sql, Map<String, Object> paramMap, Class<T> entity,Integer page, Integer limit) {
        return (List<T>) getHibernateTemplate().execute((HibernateCallback) session -> {
            SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
            addEntityAndSetParam(sqlQuery,entity,paramMap);
            sqlQuery.setFirstResult((page == null ? 0 : page) * (limit == null ? 10 : limit));
            sqlQuery.setMaxResults((limit == null ? 10 : limit));
            return sqlQuery.list();
        });
    }


    @Override
    public <T> T executeSql(CharSequence sql, Map<String, Object> paramMap, Class<T> entity) {
        return (T) getHibernateTemplate().execute(session -> {
            SQLQuery sqlQuery = session.createSQLQuery(sql.toString());
            addEntityAndSetParam(sqlQuery,entity,paramMap);
            if (judgeNotSelectQl(sql)) {
                return sqlQuery.executeUpdate();
            }
            sqlQuery.setMaxResults(1);
            Object result = sqlQuery.uniqueResult();
            return result != null && result.getClass().equals(BigInteger.class) ? Long.valueOf(result.toString()) : result;
        });
    }

    private static void addEntityAndSetParam(SQLQuery sqlQuery, Class entity, Map<String, Object> paramMap){
        if (entity != null){
            if (entity.isAnnotationPresent(Entity.class)) {
                sqlQuery.addEntity(entity);
            }else {
                setEntityScalar(sqlQuery,entity);
            }
        }
        settingParameters(sqlQuery,paramMap);
    }
    private static void setEntityScalar(SQLQuery sqlQuery,Class entity){
        for (Field f : entity.getFields()) {
            if (f.isAnnotationPresent(Transient.class)) {
                continue;
            }
            sqlQuery.addScalar(f.getName());
        }
        sqlQuery.setResultTransformer(Transformers.aliasToBean(entity));
    }

    @Override
    public Map<String, Object> paramMap() {
        return new HashMap<>();
    }

    //生成分页语句
    private static CharSequence generatePagingStatement(CharSequence ql){
        String qlStr = ql.toString();
        String lowerCase = qlStr.toLowerCase();
        StringBuilder sb = new StringBuilder(200);
        int endIndex;
        int groupIndex = lowerCase.indexOf("group by");
        if (groupIndex >= 0) {
            endIndex = groupIndex;
        }else {
            endIndex = lowerCase.indexOf("order by");
        }
        int beginIndex = lowerCase.indexOf("from");
        sb.append("select count(*) ").append(qlStr.substring(beginIndex, endIndex < 0 ? lowerCase.length() : endIndex));
        return sb;
    }

    //设置参数
    private static Query settingParameters(Query query, Map<String, Object> paramMap){
        String ql = query.getQueryString();
        if (paramMap != null) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                //sql占位符参数不存在则跳过
                if (ql.contains(":" +entry.getKey())) {
                    Object value = entry.getValue();
                    if(value instanceof Collection){
                        Collection collection = (Collection) value;
                        query.setParameterList(entry.getKey(),  collection);
                    }else {
                        query.setParameter(entry.getKey(), value);
                    }
                }
            }
        }
        return query;
    }


//    /**
//     * 测试中--两表联合查询HQL拼写
//     * @param classT
//     * @param classInner
//     * @param propertyT
//     * @param propertyInner
//     * @param <T>
//     * @return
//     */
//    public <T> String getJoinTableHql(Class classT,Class classInner,String propertyT,String propertyInner){
//        if(classT==null){
//            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"获取基本HQL出错，参数不全");
//        }
//        if(classInner==null){
//            throw new BusinessException(CommonErrorResult.SECRET_FAIL,"获取基本HQL出错，参数不全");
//        }
//        StringBuilder stringBuffer = new StringBuilder();
//        stringBuffer.append("from ").append(classT.getSimpleName()).append(" a , ").append(classInner.getSimpleName()).append(" b where a."+propertyT+" = b."+propertyInner+" and a.delFlag=").append(CommonDeleteFlag.NOT_DELETED.getCode()).append(" and b.delFlag= ").append(CommonDeleteFlag.NOT_DELETED.getCode()+" ");
//        String baseHql = stringBuffer.toString();
//        return baseHql;
//    }


    @SuppressWarnings("unchecked")
    public static String getTableName(Class clazz) {
        Table annotation = (Table) clazz.getAnnotation(Table.class);
        if (annotation != null) {
            return annotation.name();
        }

        return null;
    }

}
