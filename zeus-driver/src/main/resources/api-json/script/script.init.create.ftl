{
    "jsonrpc": "2.0",
    "method": "script.create",
    "params": [
        {
            "name": "__offline_status__", <#--上下线 调用 webapp-->
            "command": "curl -H \\"Content-Type:application/json\\" -X POST --data '{\\"hostname\\":\\"{HOST.HOST}\\"}' http://127.0.0.1:9090/device/status",
            "type": 0,
            "execute_on": 1
        },{
            "name": "__trigger_webhook__", <#-- 告警 回调 -->
            "command": "curl -H \\"Content-Type:application/json\\" -X POST --data '{\\"hostname\\":\\"{HOST.HOST}\\"}' http://127.0.0.1:9090/device/problem",
            "type": 0,
            "execute_on": 1
        },{
            "name": "__trigger_webhook__", <#-- 执行 方法 -->
            "command": "curl -H \\"Content-Type:application/json\\" -X POST --data '{\\"hostname\\":\\"{HOST.HOST}\\"}' http://127.0.0.1:12800/device/action/exec",
            "type": 0,
            "execute_on": 1
        }
    ],
    "auth": "${userAuth}",
    "id": 1
}