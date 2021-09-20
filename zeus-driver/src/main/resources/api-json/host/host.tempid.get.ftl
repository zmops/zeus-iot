<#--通过主机ID查询模板ID-->
{
    "jsonrpc": "2.0",
    "method": "host.get",
    "params": {
        "output": [
            "hostid"
        ],
        "selectParentTemplates": [
            "templateid",
            "name"
        ],
        "filter": {
            "host": [
                "${hostname}"
            ]
        }
    },
    "auth": "${userAuth}",
    "id": 1
}