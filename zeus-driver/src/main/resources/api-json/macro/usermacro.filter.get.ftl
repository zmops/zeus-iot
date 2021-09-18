{
    "jsonrpc": "2.0",
    "method": "usermacro.get",
    "params": {
        "output": [
            "macro",
            "value",
            "description"
        ],
        "hostids": "${hostId}",
        "filter": {
            "macro": "${macro}"
        }
    },
    "auth": "${userAuth}",
    "id": 1
}