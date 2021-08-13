{
    "jsonrpc": "2.0",
    "method": "hostgroup.delete",
    "params": [
        <#if hostgGroupIds??>
            <#list hostgGroupIds as hostgroupid>
                "${hostgroupid}"<#if hostgroupid_has_next>,</#if>
            </#list>
        </#if>
    ],
    "auth": "${userAuth}",
    "id": 1
}