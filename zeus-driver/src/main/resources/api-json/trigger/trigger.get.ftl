{
    "jsonrpc": "2.0",
    "method": "trigger.get",
    "params": {
        "selectHosts":["host"],
        <#if triggerIds??>
            "triggerids":${triggerIds},
        </#if>
        <#if host??>
            "host":"${host}",
        </#if>
        "output": "extend"
    },
    "id": 1,
    "auth": "${userAuth}"
}