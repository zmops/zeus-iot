{
    "jsonrpc": "2.0",
    "method": "host.update",
    "params": {
        "hostid": "${hostId}",
        "macros": [
            <#if macros??>
                <#list macros as macro>
                    {
                        "macro": "${macro.key}",
                        "value": "${macro.value}",
                        "description": "${macro.desc}"
                    }<#if macro_has_next>,</#if>
                </#list>
            </#if>
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}