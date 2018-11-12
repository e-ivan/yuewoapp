package com.war4.repository.impl;

import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * Created by dly on 2016/8/15.
 */
@Repository
@SuppressWarnings("unchecked")
public class StoreGoodRepositoryImpl extends HibernateDaoSupport {
//
//    @Override
//    public List<StoreGoodClass> queryAllGoodClass(){
//            String hql = "from StoreGoodClass where delFlag=0";
//            return (List<StoreGoodClass>)getHibernateTemplate().execute(new HibernateCallback() {
//                @Override
//                public Object doInHibernate(Session session) throws HibernateException {
//                    Query query = session.createQuery(hql);
//                    return query.list();
//                }
//            });
//    }
//
//    @Override
//    public CutPage<StoreGoodBase> queryGoodsByConditions(String keyWord,String sortCondition,String fkClassId,String fkStoreId,CutPage cutPage){
//        StringBuffer hql = new StringBuffer("from StoreGoodBase where delFlag=0 and sortCountScore>0 and sortCountLowPrice>0 ");
//        if(!StringUtils.isEmpty(keyWord)){
//            hql.append("and name like '%"+keyWord+"%' ");
//        }
//        if(!StringUtils.isEmpty(fkClassId)){
//            hql.append("and fkClassId = '"+fkClassId+"' ");
//        }
//        if(!StringUtils.isEmpty(fkStoreId)){
//            hql.append("and fkStoreId = '"+fkStoreId+"' ");
//        }
//        if(sortCondition !=null){
//            if(sortCondition.equals(StoreGoodSort.SORT_SCORE.getValue())){
//                hql.append("order by sortCountScore desc ");
//            }else
//            if(sortCondition.equals(StoreGoodSort.SORT_SELL_COUNT.getValue())){
//                hql.append("order by sortSellCount desc ");
//            }else
//            if(sortCondition.equals(StoreGoodSort.SORT_PRICE.getValue())){
//                hql.append("order by sortCountLowPrice ");
//            }
//        }
//        List<StoreGoodBase> storeGoodBases = (List<StoreGoodBase>)getHibernateTemplate().execute(new HibernateCallback() {
//            @Override
//            public Object doInHibernate(Session session) throws HibernateException {
//                Query query = session.createQuery(hql.toString());
//                query.setFirstResult(cutPage.getOffset());
//                query.setMaxResults(cutPage.getLimit());
//                return query.list();
//            }
//        });
//
//        cutPage.setiData(storeGoodBases);
//        return cutPage;
//    }

}
