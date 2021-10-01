{
    "jsonrpc": "2.0",
    "method": "item.get",
    "params": {
        "selectTags":"extend",
        "selectValueMap":"extend",
        "selectPreprocessing":"extend",
        <#if key??>
            "search":{
                "key_":"${key}"
            },
        </#if>
        <#if itemId??>
            "itemids": ${itemId},
        </#if>
        <#if hostid??>
            "hostids": ${hostid},
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
            "interfaceid",
            "error"
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}