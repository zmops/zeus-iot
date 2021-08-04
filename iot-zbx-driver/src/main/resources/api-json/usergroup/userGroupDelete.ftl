{
    "jsonrpc": "2.0",
    "method": "usergroup.delete",
    "params": [
        <#--删除用户组 ids -->
        <#if usrgrpids??>
            <#list usrgrpids as id>
                ${id} <#if id_has_next>,</#if>
            </#list>
        </#if>
    ],
    "auth": "${userAuth}",
    "id": 1
}