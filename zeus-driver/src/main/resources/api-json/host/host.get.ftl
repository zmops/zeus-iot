{
    "jsonrpc": "2.0",
    "method": "host.get",
    "params": {
        <#if groupids??>
            "filter": {
                "host":${host}
            },
        </#if>
        <#if hostid??>
            "hostids":${hostid},
        </#if>
        "output": "extend",
        "selectTags":"extend",
        "selectMacros":"extend",
        "selectValueMaps":"extend"
        },
    "auth": "${userAuth}",
    "id": 1
}