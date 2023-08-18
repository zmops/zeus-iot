{
    "jsonrpc": "2.0",
    "method": "trigger.update",
    "params": [
        {
            "triggerid": "${triggerId}",
            "description": "${ruleId}", <#--trigger name-->
            <#if ruleFunction == "nodata">
                "expression": "nodata(/${deviceId}/${itemKey},${ruleCondition}) = 1" <#--下线规则，nodata = 1 -->
            <#else>
                "expression": "last(/${deviceId}/${itemKey}) ${ruleFunction} ${ruleCondition}"
            </#if>
        },{
            "triggerid": "${recoveryTriggerId}",
            "description": "${ruleId}", <#--trigger name-->
            <#if ruleFunctionRecovery == "nodata">
                "expression": "nodata(/${deviceId}/${itemKeyRecovery},${ruleConditionRecovery}) = 0" <#-- 上线规则 nodata = 0 -->
            <#else>
                "expression": "last(/${deviceId}/${itemKeyRecovery}) ${ruleFunctionRecovery} ${ruleConditionRecovery}"
            </#if>
        }
        <#-- nodata(/Zabbix server/system.cpu.util[,nice],20s)=0 -->
    ],
    "auth": "${userAuth}",
    "id": 1
}