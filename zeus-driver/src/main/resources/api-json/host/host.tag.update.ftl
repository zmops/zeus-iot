{
    "jsonrpc": "2.0",
    "method": "host.update",
    "params": {
        "hostid":"${hostId}",
        "tags": [
            <#if tagMap??>
                <#list tagMap? keys as key>
                    {
                    "tag": "${key}",
                    "value": "${tagMap[key]}"
                    }<#if key_has_next>,</#if>
                </#list>
            </#if>
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}