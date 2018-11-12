package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.base.CutPage;
import com.war4.enums.TaskListType;
import com.war4.pojo.TaskList;
import com.war4.service.TaskListService;
import com.war4.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hh on 2017.10.21 0021.
 */
@Service
public class TaskListServiceImpl extends BaseService implements TaskListService {
    @Override
    @Transactional
    public void createTask(TaskList taskList) {
        TaskList tl = new TaskList();
        tl.setRemark(taskList.getRemark());
        tl.setObjectId(taskList.getObjectId());
        tl.setObjectTime(taskList.getObjectTime());
        tl.setState(TaskList.CREATE);
        tl.setType(taskList.getType());
        tl.setUserId(taskList.getUserId());
        baseRepository.saveObj(tl);
    }

    @Override
    public CutPage<TaskList> queryTaskList(Byte state, TaskListType type, Integer page, Integer limit) {
        StringBuilder hql = new StringBuilder(50);
        Map<String,Object> map = new HashMap<>();
        map.put("type",type.getValue());
        hql.append("from TaskList where type = :type");
        if (state != null){
            hql.append(" and state = :state");
            map.put("state",state);
        }
        hql.append(" order by created");
        return baseRepository.executeHql(hql,map,page,limit);
    }

    @Override
    public List<TaskList> queryTaskListByDate(Byte state, TaskListType type, Date startDate, Date endDate) {
        StringBuilder hql = new StringBuilder(50);
        Map<String,Object> map = new HashMap<>();
        map.put("type",type.getValue());
        hql.append("from TaskList where type = :type");
        if (state != null){
            hql.append(" and state = :state");
            map.put("state",state);
        }
        if (startDate != null) {
            hql.append(" and created >= :startDate");
            map.put("startDate", startDate);
        }
        if (endDate != null) {
            hql.append(" and created <= :endDate");
            map.put("endDate", DateUtil.endOfDay(endDate));
        }
        hql.append(" order by created");
        return baseRepository.queryHqlResult(hql,map,0,CutPage.MAX_COUNT);
    }

    @Override
    @Transactional
    public void updateTaskFinish(TaskList taskList) {
        taskList.setState(TaskList.FINISH);
        taskList.setUpdated(new Date());
        baseRepository.updateObj(taskList);
    }

    @Override
    public void restTask(TaskList taskList) {
        taskList.setState(TaskList.CREATE);
        taskList.setUpdated(new Date());
        baseRepository.updateObj(taskList);
    }
}
