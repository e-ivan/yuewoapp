package com.war4.repository.impl;

import com.war4.pojo.FileUpload;
import com.war4.repository.FileRepository;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by dly on 2016/8/25.
 */
@Repository
@SuppressWarnings("unchecked")
public class FileRepositoryImpl extends HibernateDaoSupport implements FileRepository {
    //查询文件
    public List<FileUpload> getFilesByConditions(String fileType,String filePurpose,String fkPurposeId,Integer limit){
        String hql = "from FileUpload where fileType=? and purpose=? and fkPurposeId=? order by createTime desc";
        return (List<FileUpload>)getHibernateTemplate().execute(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException {
                Query query = session.createQuery(hql);
                query.setParameter(0,fileType);
                query.setParameter(1,filePurpose);
                query.setParameter(2,fkPurposeId);
                if(limit != null){
                    query.setMaxResults(limit);
                }
                return query.list();
            }
        });
    }


    @Override
    //插入文件信息
    public void insertFile(FileUpload fileUpload){
        getHibernateTemplate().save(fileUpload);
    }
}
