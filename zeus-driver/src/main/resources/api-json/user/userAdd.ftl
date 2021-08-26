{
    "jsonrpc": "2.0",
    "method": "user.create",
    "params": {
        "username": "${name}",
        "passwd": "${password}",
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