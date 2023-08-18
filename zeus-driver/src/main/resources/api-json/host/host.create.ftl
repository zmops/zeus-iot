{
    "jsonrpc": "2.0",
    "method": "host.create",
    "params": {
        "host": "${hostName}",
        <#if interfaces??>
        "interfaces": [
            {
                "type": ${interfaces.type},
                "main": ${interfaces.main},
                "useip": ${interfaces.useip},
                "ip": "${interfaces.ip}",
                "dns": "",
                "port": "${interfaces.port}"
            }
        ],
        </#if>
        <#if proxyid??>
        "proxy_hostid":"${proxyid}",
        </#if>
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