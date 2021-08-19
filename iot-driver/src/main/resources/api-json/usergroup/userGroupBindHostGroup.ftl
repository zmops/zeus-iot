{
    "jsonrpc": "2.0",
    "method": "usergroup.update",
    "params": {
        "rights": [
            <#if hostGroupIds??>
                <#list hostGroupIds as hostGrpId>
                    {
                        "id":"${hostGrpId}",
                        "permission":"3" <#--读写权限-->
                    }
                    <#if hostGrpId_has_next>,</#if>
                </#list>
            </#if>
        ],
        "usrgrpid": "${userGroupId}"
    },
    "auth": "${userAuth}",
    "id": 1
}