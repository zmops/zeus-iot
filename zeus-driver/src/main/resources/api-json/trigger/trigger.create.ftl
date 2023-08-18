{
    "jsonrpc": "2.0",
    "method": "trigger.create",
    "params": {
        "description": "${triggerName}",
        "expression": "${expression}",
        "priority" : ${ruleLevel},
        "manual_close":1
    },
    "auth": "${userAuth}",
    "id": 1
}