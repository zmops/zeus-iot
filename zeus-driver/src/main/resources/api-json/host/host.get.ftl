{
    "jsonrpc": "2.0",
    "method": "host.get",
    "params": {
        <#if host??>
        "filter": {
            "host": "${host}"
        },
        </#if>
        <#if hostid??>
        "hostids": "${hostid}",
        </#if>
        "output": "extend",
        "selectTags":"extend",
        "selectMacros":"extend",
        "selectValueMaps":"extend",
        "selectInterface":"extend"
    },
    "auth": "${userAuth}",
    "id": 1
}