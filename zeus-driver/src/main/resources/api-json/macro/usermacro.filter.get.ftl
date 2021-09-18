{
    "jsonrpc": "2.0",
    "method": "usermacro.get",
    "params": {
        "output": [
            "macro",
            "value",
            "description"
        ],
        "hostids": "${hostid}",
        <#if macro??>
        "filter": {
            "macro": "${macro}"
        }
        </#if>
    },
    "auth": "${userAuth}",
    "id": 1
}