{
    "jsonrpc": "2.0",
    "method": "trigger.create",
    "params": [
        {
            "description": "${ruleId}", <#--trigger name-->
            <#if ruleFunction == "nodata">
                "expression": "nodata(/${deviceId}/${itemKey},${ruleCondition}) = 1", <#--下线规则，nodata = 1 -->
            <#else>
                "expression": "last(/${deviceId}/${itemKey}) ${ruleFunction} ${ruleCondition}",
            </#if>
            "recovery_mode": 2,
            "type": 1,
            "tags": [
                {
                    "tag": "__offline__",
                    "value": "{HOST.HOST}" <#--device id-->
                }
            ]
        },
        {
            "description": "${ruleId}", <#--trigger name-->
            "recovery_mode": 2,
            "type": 1,
            <#if ruleFunctionRecovery == "nodata">
                "expression": "nodata(/${deviceId}/${itemKeyRecovery},${ruleConditionRecovery}) = 0", <#-- 上线规则 nodata = 0 -->
            <#else>
                "expression": "last(/${deviceId}/${itemKeyRecovery}) ${ruleFunctionRecovery} ${ruleConditionRecovery}",
            </#if>
            "tags": [
                {
                    "tag": "__online__",
                    "value": "{HOST.HOST}" <#--device id-->
                }
            ]
        }
        <#-- nodata(/Zabbix server/system.cpu.util[,nice],20s)=0 -->
    ],
    "auth": "${userAuth}",
    "id": 1
}