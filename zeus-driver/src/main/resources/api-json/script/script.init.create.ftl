{
    "jsonrpc": "2.0",
    "method": "script.create",
    "params": [
        {
            "name": "__offline_status__", <#--上下线 调用 webapp-->
            "command": "curl -H \\"Content-Type:application/json\\" -X POST --data '{\\"hostname\\":\\"{HOST.HOST}\\",\\"recovery\\":\\"{EVENT.RECOVERY.STATUS}\\"}' http://127.0.0.1:9090/device/status",
            "type": 0,
            "execute_on": 1
        },{
            "name": "__trigger_webhook__", <#-- 告警 回调 -->
            "command": "curl -H \\"Content-Type:application/json\\" -X POST --data '{\\"hostname\\":\\"{HOST.HOST}\\",\\"triggerName\\":\\"{TRIGGER.NAME}\\"}' http://127.0.0.1:9090/device/problem",
            "type": 0,
            "execute_on": 1
        },{
            "name": "__trigger_execute__", <#-- 执行 方法 -->
            "command": "curl -H \\"Content-Type:application/json\\" -X POST --data '{\\"triggerName\\":\\"{TRIGGER.NAME}\\",\\"triggerId\\":\\"{TRIGGER.ID}\\"}' http://127.0.0.1:12800/device/action/exec",
            "type": 0,
            "execute_on": 1
        },{
            "name": "__attr_event__", <#-- 属性事件 回调 -->
            "command": "curl -H \\"Content-Type:application/json\\" -X POST --data '{\\"hostname\\":\\"{HOST.HOST}\\",\\"itemName\\":\\"{ITEM.NAME}\\"}' http://127.0.0.1:12800/device/event",
            "type": 0,
            "execute_on": 1
        }
    ],
    "auth": "${userAuth}",
    "id": 1
}