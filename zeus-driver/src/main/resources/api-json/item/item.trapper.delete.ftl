{
    "jsonrpc": "2.0",
    "method": "item.delete",
    "params": [
        <#if itemIds??>
            <#list itemIds as itemid>
                "${itemid}"<#if itemid_has_next>,</#if>
            </#list>
        </#if>
    ],
    "auth": "${userAuth}",
    "id": 1
}