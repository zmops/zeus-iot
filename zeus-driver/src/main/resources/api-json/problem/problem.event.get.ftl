{
    "jsonrpc": "2.0",
    "method": "problem.get",
    "params": {
        "output": "extend",
        "selectTags": "extend",
        "recent": ${recent},
        "sortfield": ["eventid"],
        <#if hostId??>
            "hostids":${hostId},
        </#if>
        <#if timeFrom??>
            "time_from":${timeFrom},
        </#if>
        <#if timeTill??>
            "time_till":${timeTill},
        </#if>
        "tags":[{"tag": "__event__"}],
        "filter":{
            "source":"0"
        },
        "sortorder": "DESC"
    },
    "auth": "${userAuth}",
    "id": 1
}