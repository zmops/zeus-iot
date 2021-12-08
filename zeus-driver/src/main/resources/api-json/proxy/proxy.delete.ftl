{
    "jsonrpc": "2.0",
    "method": "proxy.delete",
    "params": [
        <#if hostIds??>
            <#list hostIds as hostId>
                "${hostId}"<#if hostId_has_next>,</#if>
            </#list>
        </#if>
    ],
    "auth": "${userAuth}",
    "id": 1
}