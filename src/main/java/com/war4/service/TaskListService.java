package com.war4.service;

import com.war4.base.CutPage;
import com.war4.enums.TaskListType;
import com.war4.pojo.TaskList;

import java.util.Date;
import java.util.List;

/**
 * 任务列表服务
 * Created by hh on 2017.10.21 0021.
 */
public interface TaskListService {
    void createTask(TaskList taskList);

    /**
     * 查询任务列表
     * @param state
     * @param type
     */
    CutPage<TaskList> queryTaskList(Byte state, TaskListType type, Integer page, Integer limit);

    List<TaskList> queryTaskListByDate(Byte state, TaskListType type, Date startDate, Date endDate);

    /**
     * 更新任务为完成
     * @param taskList
     */
    void updateTaskFinish(TaskList taskList);

    /**
     * 重置任务
     * @param taskList
     */
    void restTask(TaskList taskList);
}
