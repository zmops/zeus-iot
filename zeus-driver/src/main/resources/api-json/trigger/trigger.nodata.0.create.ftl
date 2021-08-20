{
    "jsonrpc": "2.0",
    "method": "trigger.create",
    "params": [
        {
            "description": "${triggerName}", <#--多长时间内有数据-->
            "expression": "nodata(/${hostName}/${itemKey},${nodataTime})=0",
            "recovery_mode": 2,
            "status": 1
        }
    ],
    "auth": "${userAuth}",
    "id": 1
}