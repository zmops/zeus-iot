{
    "jsonrpc": "2.0",
    "method": "item.create",
    "params": {
        "name": "${itemName}",
        "key_": "${itemKey}",
        "hostid": "${hostId}",
        "type": ${source},
        <#if interfaceid??>
        "interfaceid": "${interfaceid}",
        </#if>
        "trends":"0",
        <#if delay??>
        "delay":"${delay}",
        </#if>
        "history":"30d",
        <#if source == '18'>
            "master_itemid":${masterItemid},
        </#if>
        "value_type": ${valueType},
        <#if valuemapid?? && valuemapid != ''>
            "valuemapid":${valuemapid},
        </#if>
        <#if valueType == '0' || valueType == '3'>
        <#if units??>
            "units": "${units}",
        </#if>
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
        ],
        "tags": [
        <#if tagMap??>
            <#list tagMap? keys as key>
                {
                "tag": "${key}",
                "value": "${tagMap[key]}"
                }<#if key_has_next>,</#if>
            </#list>
        </#if>
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}