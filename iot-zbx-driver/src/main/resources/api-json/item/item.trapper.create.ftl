{
    "jsonrpc": "2.0",
    "method": "item.create",
    "params": {
        "name": "${itemName}",
        "key_": "${itemKey}",
        "hostid": "${hostId}",
        "type": 2,  <#--zabbix trapper-->
        "value_type": ${valueType},
        <#if valueType == '0' || valueType == '3'>
            "units": "${units}",
        </#if>
        "preprocessing": [
        <#if processList??>
            <#list processList as process>
            {
                "type": ${process.type},
                "params": "${process.params}",
                "error_handler": "0",
                "error_handler_params": ""
            }<#if process_has_next>,</#if>
            </#list>
        </#if>
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}