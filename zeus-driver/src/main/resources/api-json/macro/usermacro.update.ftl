{
    "jsonrpc": "2.0",
    "method": "usermacro.update",
    "params": {
        "hostmacroid": "${macroid}",
        "macro": "${macro}",
        "value": "${value}",
        <#if description??>
        "description":"${description}"
        </#if>
    },
    "auth": "${userAuth}",
    "id": 1
}