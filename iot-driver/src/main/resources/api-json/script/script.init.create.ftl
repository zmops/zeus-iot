{
    "jsonrpc": "2.0",
    "method": "script.create",
    "params": {
        "name": "__offline_status__",
        "command": "curl -H \\"Content-Type:application/json\\" -X POST --data '{\\"hostname\\":\\"{HOST.HOST}\\",\\"deviceStatus\\":\\"{EVENT.ID}\\"}' http://${zeusServerIp}:${zeusServerPort}/rest/device/webhook",
        "type": 0,
        "execute_on": 1
    },
    "auth": "${userAuth}",
    "id": 1
}