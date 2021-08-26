{
    "jsonrpc": "2.0",
    "method": "user.create",
    "params": {
        "username": "cookie",
        "passwd": "cookie",
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