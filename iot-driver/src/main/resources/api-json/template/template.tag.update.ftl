{
    "jsonrpc": "2.0",
    "method": "template.update",
    "params": {
        "templateid": "${templateId}",
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