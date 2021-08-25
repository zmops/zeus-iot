package com.zmops.iot.web.alarm.service;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.domain.messages.MessageBody;
import com.zmops.iot.domain.messages.Messages;
import com.zmops.iot.domain.messages.query.QMessages;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.alarm.dto.param.MessageParam;
import com.zmops.iot.web.alarm.socket.WebSocketServer;
import io.ebean.DB;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 消息服务
 **/
@Service
public class MessageService {

    protected final static int sys = 1;

    /**
     * 发送消息
     *
     * @param body
     */
    public void push(MessageBody body) {
        Objects.requireNonNull(body.getMsg());
        if (body.isPersist()) {
            List<Long> tos;
            if (!CollectionUtils.isEmpty(body.getTo())) {
                tos = body.getTo();
            } else {
                List<SysUser> userList = new QSysUser().findList();
                tos = userList.parallelStream().map(SysUser::getUserId).collect(Collectors.toList());
            }
            tos.forEach(to -> {
                Messages messages = new Messages();
                messages.setClassify(sys);
                messages.setTitle(body.getMsg());
                messages.setUserId(to);
                String content = "";
                if (ToolUtil.isNotEmpty(body.getBody())) {
                    content = JSON.toJSONString(body.getBody());
                    messages.setContent(content);
                }
                messages.setClock(System.currentTimeMillis() / 1000);
                saveMessage(messages);
            });
        }
        body.addBody("classify", sys);

        if (!CollectionUtils.isEmpty(body.getTo())) {
            body.getTo().forEach(to -> {
                WebSocketServer.sendMessageTo(JSON.toJSONString(body), to + "");
            });
        } else {
            WebSocketServer.sendMessageToAll(JSON.toJSONString(body));
        }
    }

    /**
     * 消息列表
     *
     * @param messageParam
     * @return
     */
    public Pager<Messages> list(MessageParam messageParam) {
        LoginUser user      = LoginContextHolder.getContext().getUser();
        QMessages qMessages = new QMessages();
        qMessages.userId.eq(user.getId());

        if (messageParam.getReaded() != null) {
            qMessages.readed.eq(messageParam.getReaded());
        }
        qMessages.orderBy(" readed asc, clock desc");
        List<Messages> list = qMessages.setFirstRow((messageParam.getPage() - 1) * messageParam.getMaxRow())
                .setMaxRows(messageParam.getMaxRow()).findList();

        return new Pager<>(list, qMessages.findCount());
    }

    /**
     * 读消息
     *
     * @param messageParam
     * @return
     */
    public List<Integer> read(MessageParam messageParam) {
        LoginUser user     = LoginContextHolder.getContext().getUser();
        Messages  messages = new Messages();
        messages.setReaded(1);
        QMessages qMessages = new QMessages();
        if (!CollectionUtils.isEmpty(messageParam.getIds())) {
            qMessages.id.in(messageParam.getIds());
        }
        qMessages.userId.eq(user.getId());

        new QMessages().asUpdate().set("readed", 1).update();
        return messageParam.getIds();
    }

    /**
     * 未读消息数
     *
     * @return
     */
    public Map<String, Object> unReadNum() {
        LoginUser           user  = LoginContextHolder.getContext().getUser();
        Map<String, Object> map   = new HashMap<>();
        int                 count = new QMessages().readed.eq(0).userId.eq(user.getId()).findCount();
        map.put("count", count);
        return map;
    }

    /**
     * 消息详情
     *
     * @param id
     * @return
     */
    public Messages info(Integer id) {
        return new QMessages().id.eq(id).findOne();
    }


    private void saveMessage(Messages messages) {
        DB.save(messages);
    }

}
