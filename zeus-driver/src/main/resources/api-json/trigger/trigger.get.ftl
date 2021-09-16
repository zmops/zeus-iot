{
    "jsonrpc": "2.0",
    "method": "trigger.get",
    "params": {
        "selectHosts":["host"],
    <#if triggerids??>
        "triggerids":[${triggerids}],
    </#if>
    <#if host??>
        "host":${host},
    </#if>
        "output": "extend"
    },
    "id": 1,
    "auth": "${userAuth}"
}