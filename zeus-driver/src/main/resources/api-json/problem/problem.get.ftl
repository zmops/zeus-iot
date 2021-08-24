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
        "sortorder": "DESC"
    },
    "auth": "${userAuth}",
    "id": 1
}