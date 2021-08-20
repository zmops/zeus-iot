{
    "jsonrpc": "2.0",
    "method": "trigger.create",
    "params": [
        {
            "description": "${triggerName}",
            "expression": "last(/${hostName}/${itemKey})=${dataValue}",
            "recovery_mode": 2,
            "status": 1
        }
    ],
    "auth": "${userAuth}",
    "id": 1
}