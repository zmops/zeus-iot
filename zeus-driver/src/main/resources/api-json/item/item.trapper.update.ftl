{
    "jsonrpc": "2.0",
    "method": "item.update",
    "params": {
        "itemid": "${itemid}",
        "name": "${itemName}",
        "key_": "${itemKey}",
        "hostid": "${hostId}",
        <#if interfaceid??>
            "interfaceid": "${interfaceid}",
        </#if>
        "type": ${source},
        <#if delay??>
            "delay":"${delay}",
        </#if>
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