{
    "jsonrpc": "2.0",
    "method": "host.create",
    "params": {
        "host": "${hostName}",
        "groups": [
            <#if groupids??>
                <#list groupids as groupid>
                    { "groupid": "${groupid}" }<#if groupid_has_next>,</#if>
                </#list>
            </#if>
        ],
        "templates": [
            {
                "templateid": "${templateid}"
            }
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}