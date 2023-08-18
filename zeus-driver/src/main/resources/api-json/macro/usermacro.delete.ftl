{
    "jsonrpc": "2.0",
    "method": "usermacro.delete",
    "params": [
        <#list macroids as id>
            "${id}"<#if id_has_next>,</#if>
        </#list>
    ],
    "auth": "${userAuth}",
    "id": 1
}