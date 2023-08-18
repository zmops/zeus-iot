{
    "name": "${runnerName}",
    "batch_interval": ${batchInterval},
    "batch_size":   ${batchSize},
    "extra_info": false,
    "reader": {
        "log_path": "${logPath}",
        "read_from": "oldest",
        "datasource_tag": "datasource",
        "encoding": "UTF-8",
        "mode": "file"
    },
    "cleaner": {},
    "parser": {
        "type": "json",
        "keep_raw_data": "false",
        "name": "parser",
        "disable_record_errdata": "false"
    },
    "transforms": [
        {
            "key": "groups,applications,datasource,name",
            "type": "discard"
        },
        {
            "key": "type",
            "mode": "keep",
            "pattern": "^[0|3]$",
            "type": "filter"
        },
        {
            "key": "source",
            "override": false,
            "type": "label",
            "value": "s1"
        }
    ],
    "senders": [{
        "http_sender_url": "http://127.0.0.1:12800/zabbix/data-transfer",
        "http_sender_protocol": "json",
        "http_sender_escape_html": "true",
        "http_sender_csv_split": ",",
        "http_sender_gzip": "true",
        "http_sender_timeout": "30s",
        "ft_strategy": "backup_only",
        "ft_discard_failed_data": "false",
        "ft_memory_channel": "false",
        "ft_long_data_discard": "false",
        "max_disk_used_bytes": "524288000",
        "max_size_per_file": "104857600",
        "sender_type": "http"
    }]
}