package com.zmops.iot.web.task.service;

import cn.hutool.core.bean.BeanUtil;
import com.zmops.iot.domain.schedule.Task;
import com.zmops.iot.domain.schedule.query.QTask;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.task.dto.TaskDto;
import io.ebean.DB;
import org.springframework.stereotype.Service;

/**
 * @author yefei
 **/
@Service
public class TaskService {

    /**
     * 创建任务
     *
     * @param taskDto
     * @return
     */
    public Integer createTask(TaskDto taskDto) {
        Task task = new Task();
        BeanUtil.copyProperties(taskDto, task);
        DB.insert(task);
        return task.getId();
    }

    /**
     * 修改任务
     *
     * @param taskDto
     * @return
     */
    public TaskDto updateTask(TaskDto taskDto) {
        Task task = new Task();
        BeanUtil.copyProperties(taskDto, task);
        DB.update(task);
        return taskDto;
    }

    /**
     * 删除任务
     *
     * @param taskParam
     */
    public void deleteTask(TaskDto taskParam) {
        if (taskParam.getId() == null) {
            return;
        }
        new QTask().id.eq(taskParam.getId()).delete();
    }

    /**
     * 启用 禁用任务
     * 默认启用
     *
     * @param taskParam
     */
    public void status(TaskDto taskParam) {
        if (taskParam.getId() == null) {
            return;
        }
        String triggerStatus = taskParam.getTriggerStatus();
        if (ToolUtil.isEmpty(triggerStatus)) {
            triggerStatus = CommonStatus.DISABLE.getCode();
        }
        DB.update(Task.class).where().eq("id", taskParam.getId()).asUpdate().set("trigger_status", triggerStatus).update();
    }
}
