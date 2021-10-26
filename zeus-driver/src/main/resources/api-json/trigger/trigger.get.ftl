{
    "jsonrpc": "2.0",
    "method": "trigger.get",
    "params": {
        "selectHosts":["host"],
        "selectTags":["tag"],
        <#if triggerIds??>
            "triggerids":${triggerIds},
        </#if>
        <#if host??>
            "host":"${host}",
        </#if>
        <#if description??>
        "filter":{
            "description":${description}
        },
        </#if>
        "output": "extend"
    },
    "id": 1,
    "auth": "${userAuth}"
}