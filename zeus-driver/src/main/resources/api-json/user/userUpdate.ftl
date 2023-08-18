{
    "jsonrpc": "2.0",
    "method": "user.update",
    "params": {
        "userid": "${userId}",
        "roleid": "${roleId}",
        "usrgrps": [
            {
                "usrgrpid": "${usrGrpId}"
            }
        ]
    },
    "id": 1,
    "auth": "${userAuth}"
}