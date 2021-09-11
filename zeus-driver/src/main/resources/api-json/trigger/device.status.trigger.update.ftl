{
    "jsonrpc": "2.0",
    "method": "trigger.create",
    "params": [
        {
            "triggerid": "${triggerId}",
            "description": "${rule.ruleId}", <#--trigger name-->
            <#if rule.ruleFunction == "nodata">
                "expression": "nodata(/${rule.deviceId}/${rule.itemKey},${rule.ruleCondition}) = 1", <#--下线规则，nodata = 1 -->
            <#else>
                "expression": "last(/${rule.deviceId}/${rule.itemKey}) ${rule.ruleFunction} ${rule.ruleCondition}",
            </#if>
            "recovery_mode": 1,
            <#if rule.ruleFunctionSecond == "nodata">
                "recovery_expression": "nodata(/${rule.deviceId}/${rule.itemKeySecond},${rule.ruleConditionSecond}) = 0" <#-- 上线规则 nodata = 0 -->
            <#else>
                "recovery_expression": "last(/${rule.deviceId}/${rule.itemKeySecond}) ${rule.ruleFunctionSecond} ${rule.ruleConditionSecond}"
            </#if>
        }
        <#-- nodata(/Zabbix server/system.cpu.util[,nice],20s)=0 -->
    ],
    "auth": "${userAuth}",
    "id": 1
}