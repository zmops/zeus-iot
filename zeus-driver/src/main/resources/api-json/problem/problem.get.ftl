{
    "jsonrpc": "2.0",
    "method": "problem.get",
    "params": {
        "output": "extend",
        "selectAcknowledges": "extend",
        "selectTags": "extend",
        "selectSuppressionData": "extend",
        "recent": "true",
        "sortfield": ["eventid"],
        <#if hostId??>
            "hostids":"${hostId}"
        </#if>
        <#if timeFrom??>
            "time_from":${timeFrom},
        </#if>
        "filter":{
            "source":"0"
        },
        "sortorder": "DESC"
    },
    "auth": "${userAuth}",
    "id": 1
}