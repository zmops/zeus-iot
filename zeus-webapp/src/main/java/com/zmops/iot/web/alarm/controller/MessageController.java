package com.zmops.iot.web.alarm.controller;


import com.zmops.iot.domain.messages.Messages;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.web.alarm.dto.param.MessageParam;
import com.zmops.iot.web.alarm.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author yefei
 **/
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    MessageService messageService;

    /**
     * 消息分页列表
     *
     * @param messageParam
     * @return
     */
    @RequestMapping("/getMessageByPage")
    public Pager getMessageByPage(@RequestBody MessageParam messageParam) {
        return messageService.list(messageParam);
    }

    /**
     * 消息详情
     *
     * @param id
     * @return
     */
    @RequestMapping("/info")
    public Messages info(@RequestParam("id") Integer id) {
        return messageService.info(id);
    }


    /**
     * 消息置为已读
     *
     * @param messageVo
     * @return
     */
    @RequestMapping("/read")
    public List<Integer> read(@RequestBody MessageParam messageVo) {
        return messageService.read(messageVo);
    }

    /**
     * 未读消息数
     *
     * @return
     */
    @GetMapping("/num")
    public Map<String, Object> unReadNum() {
        return messageService.unReadNum();
    }


}
