{
    "jsonrpc": "2.0",
    "method": "action.create",
    "params": {
        "name": "${name}",
        "eventsource": 0,
        "status": 0,
        "esc_period": "2m",
        "filter": {
            "evaltype": 0,
            "conditions": [
                {
                    "conditiontype": 25,
                    "operator": 0,
                    "value": "${tagName}"
                }
            ]
        },
        "operations": [
            {
                "operationtype": 1,
                "opcommand_hst": [
                    {
                        "hostid": "0"
                    }
                ],
                "opcommand": {
                    "scriptid": "${scriptId}"
                }
            }
        ],
        "recovery_operations": [
            {
                "operationtype": "1",
                "opcommand_hst": [
                    {
                        "hostid": "0"
                    }
                ],
                "opcommand": {
                    "scriptid": "${scriptId}"
                }
            }
        ]
    },
    "auth": "${userAuth}",
    "id": 1
}