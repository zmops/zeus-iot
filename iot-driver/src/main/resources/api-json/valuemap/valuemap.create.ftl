{
    "jsonrpc": "2.0",
    "method": "valuemap.create",
    "params": {
        "hostid": "${hostId}",
        "name": "${valueMapName}",
        "mappings": [
            <#if valMaps??>
                <#list valMaps?keys as key>
                    {
                        "type": "0", <#--全部精确匹配-->
                        "value": "${key}",
                        "newvalue": "${valMaps[key]}"
                    }<#if key_has_next>,</#if>
                </#list>
            </#if>
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}