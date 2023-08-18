{
    "jsonrpc": "2.0",
    "method": "trigger.update",
    "params": {
        "triggerid": "${triggerId}",
        "tags": [
            <#list tagMap?keys as key>
            {
                "tag": "${key}",
                "value": "${tagMap[key]}"
            }<#if key_has_next>,</#if>
            </#list>
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}