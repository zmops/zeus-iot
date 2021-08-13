{
    "jsonrpc": "2.0",
    "method": "user.delete",
    "params": [
        <#--删除用户 ID-->
        <#if usrids??>
            <#list usrids as usrid>
                ${usrid} <#if usrid_has_next>,</#if>
            </#list>
        </#if>
    ],
    "id": 1,
    "auth": "${userAuth}"
}