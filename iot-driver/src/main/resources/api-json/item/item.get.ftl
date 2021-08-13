{
    "jsonrpc": "2.0",
    "method": "item.get",
    "params": {
        "selectTags":"extend",
        "selectValueMap":"extend",
        "selectPreprocessing":"extend",
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
        ],
        "itemids": "${itemId}"
    },
    "auth": "${userAuth}",
    "id": 1
}