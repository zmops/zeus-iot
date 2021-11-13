package com.zmops.iot.web.task.controller;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.dto.DeviceGroupDto;
import com.zmops.iot.web.task.dto.TaskDto;
import com.zmops.iot.web.task.param.TaskParam;
import com.zmops.iot.web.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * <p>
 * 任务管理
 **/
@RestController
@RequestMapping("/task")
public class taskController {

    @Autowired
    TaskService taskService;

//    /**
//     * 任务分页列表
//     *
//     * @return
//     */
//    @PostMapping("/getTaskByPage")
//    public Pager<DeviceGroupDto> getTaskByPage(@RequestBody TaskParam taskParam) {
//        return taskService.getTaskByPage(taskParam);
//    }
//
//    /**
//     * 设任务列表
//     *
//     * @return
//     */
//    @PostMapping("/list")
//    public ResponseData list(@RequestBody TaskParam taskParam) {
//        return ResponseData.success(taskService.list(taskParam));
//    }

    /**
     * 创建任务
     *
     * @return
     */
    @PostMapping("/create")
//    @BussinessLog(value = "创建任务")
    public ResponseData createTask(@Validated(BaseEntity.Create.class) @RequestBody TaskDto taskDto) {
        return ResponseData.success(taskService.createTask(taskDto));
    }


    /**
     * 更新任务
     *
     * @return
     */
    @PostMapping("/update")
//    @BussinessLog(value = "更新任务")
    public ResponseData updateTask(@Validated(BaseEntity.Update.class) @RequestBody TaskDto taskDto) {
        return ResponseData.success(taskService.updateTask(taskDto));
    }

    /**
     * 删除任务
     *
     * @return
     */
    @PostMapping("/delete")
//    @BussinessLog(value = "删除任务")
    public ResponseData deleteTask(@Validated(BaseEntity.Delete.class) @RequestBody TaskDto taskParam) {
        taskService.deleteTask(taskParam);
        return ResponseData.success();
    }

    /**
     * 启动 停止任务
     *
     * @return
     */
    @PostMapping("/status")
//    @BussinessLog(value = "启动 停止任务")
    public ResponseData status(@Validated(BaseEntity.Delete.class) @RequestBody TaskDto taskDto) {
        taskService.status(taskDto);
        return ResponseData.success();
    }
}
