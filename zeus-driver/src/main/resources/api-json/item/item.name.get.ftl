{
    "jsonrpc": "2.0",
    "method": "item.get",
    "params": {
        "selectHosts":["host"],
        <#if name??>
            "search":{
                "name":"${name}"
            },
        </#if>
        "output": [
            "itemid",
            "hostid",
            "name",
            "key_",
            "status",
            "value_type",
            "units",
            "valuemapid",
            "interfaceid"
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}