{
    "jsonrpc": "2.0",
    "method": "hostgroup.delete",
    "params": [
        <#if hostGroupIds??>
            <#list hostGroupIds as hostgroupid>
                "${hostgroupid}"<#if hostgroupid_has_next>,</#if>
            </#list>
        </#if>
    ],
    "auth": "${userAuth}",
    "id": 1
}