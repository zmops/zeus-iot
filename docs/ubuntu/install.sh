#!/usr/bin/env bash
set -e
# 初始化系统

function logprint() {
        if [ $? != 0 ]; then
                echo "$1"
                exit
        fi
}

# 安装前准备
## 系统环境检测
if [ "$(uname)" != Linux ]; then
        echo "该脚本只使用 Linux 系统"
        exit $E_BADOD
fi

if [ "$UID" -ne "$ROOT_UID" ]; then
        echo "Must be root to install"
        exit $E_NOTROOT
fi
### 操作系统
if [ ! -f /etc/redhat-release ]; then
        if [[ "$(cat /etc/issue)" =~ ^Ubuntu* ]]; then
                release=Ubuntu
        fi
fi
### 网络
if ! ping -c 3 mirrors.aliyun.com &>/dev/null; then
        echo "网络异常。。。"
        exit
fi

### cpu、mem、disk
cores=$(grep </proc/cpuinfo -c "processor")
memstotal=$(grep </proc/meminfo "MemTotal" | awk '{printf("%.f\n",$2/1024/1024)}')
disks=$(df -T | awk '/(xfs|ext4|ext3)/{if($3/1024/1024 > 10)printf("%s\t%d\n",$7,$3/1024/1024)}' | grep -v -c "/boot")

if [ "$cores" -lt 0 ] || [ "$memstotal" -lt 0 ] || [ "$disks" -eq 0 ]; then
        echo "要求最低配置为 CPU 2核 内存 4GB 存储空间 100G"
        exit 70
fi



function InitSystem() {
    ## 修改主机名
    if ! hostnamectl set-hostname zeus-server; then
        echo "主机名修改失败"
        exit
    fi
    ## 修改时区
    if ! ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime; then
        echo "时区修改失败"
        exit 0
    fi
    ## 更新下载源
    mv /etc/apt/sources.list /etc/apt/sources.listbak
    tee /etc/apt/sources.list <<EOL &>/dev/null
deb http://mirrors.aliyun.com/ubuntu/ focal main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal main restricted universe multiverse

deb http://mirrors.aliyun.com/ubuntu/ focal-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal-security main restricted universe multiverse

deb http://mirrors.aliyun.com/ubuntu/ focal-updates main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal-updates main restricted universe multiverse

deb http://mirrors.aliyun.com/ubuntu/ focal-proposed main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal-proposed main restricted universe multiverse

deb http://mirrors.aliyun.com/ubuntu/ focal-backports main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ focal-backports main restricted universe multiverse
EOL
}

function AddInstallRepo() {
    ## 安装PGsql源
    echo "deb http://apt.postgresql.org/pub/repos/apt/ $(lsb_release -c -s)-pgdg main" | sudo tee /etc/apt/sources.list.d/pgdg.list
    wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
    sudo sh -c "echo 'deb https://packagecloud.io/timescale/timescaledb/ubuntu/ $(lsb_release -c -s) main' > /etc/apt/sources.list.d/timescaledb.list"
    wget --quiet -O - https://packagecloud.io/timescale/timescaledb/gpgkey | sudo apt-key add -
    ## 安装zabbix 5.4 源
    wget https://repo.zabbix.com/zabbix/5.4/ubuntu/pool/main/z/zabbix-release/zabbix-release_5.4-1+ubuntu20.04_all.deb
    dpkg -i zabbix-release_5.4-1+ubuntu20.04_all.deb
    apt update

}

function PGInstall() {
    # PG 安装
    apt install timescaledb-2-postgresql-13 -y
    echo "shared_preload_libraries = 'timescaledb'" >> /etc/postgresql/13/main/postgresql.conf
    cd /tmp || exit
    sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'zeusiot';" 2> /tmp/pg.log
    systemctl restart postgresql
}

function ZbxInstall() {
    # zabbix 安装
    apt install zabbix-server-pgsql zabbix-frontend-php php7.4-pgsql zabbix-nginx-conf zabbix-sql-scripts zabbix-agent -y
    # 初始化 zabbix 配置
    cd /tmp || exit
    sudo -u postgres createuser zabbix
    sudo -u postgres psql -c "ALTER USER zabbix WITH PASSWORD 'zabbix';"
    sudo -u postgres createdb -O zabbix zabbix
    zcat /usr/share/doc/zabbix-sql-scripts/postgresql/create.sql.gz | sudo -u zabbix psql zabbix
    echo "CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;" | sudo -u postgres psql zabbix
    cat /usr/share/doc/zabbix-sql-scripts/postgresql/timescaledb.sql | sudo -u zabbix psql zabbix 1>/dev/null

    sed -i 's/^# DBPassword=/DBPassword=zabbix/g' /etc/zabbix/zabbix_server.conf
    mv /usr/share/zabbix/conf/zabbix.conf.php.example /usr/share/zabbix/conf/zabbix.conf.php
    sed -i "s/\($DB\['PASSWORD'\]\s*=\).*/\1 'zabbix';/g" /usr/share/zabbix/conf/zabbix.conf.php
    sed -i "s/\($DB\['TYPE'\]\s*=\).*/\1 \'POSTGRESQL\';/g" /usr/share/zabbix/conf/zabbix.conf.php
    # 修改 php 配置
    sed -i 's/post_max_size = 8M/post_max_size = 16M/' /etc/php/7.4/fpm/php.ini
    sed -i 's/upload_max_filesize = 2M/upload_max_filesize = 20M/' /etc/php/7.4/fpm/php.ini
    sed -i 's/max_execution_time = 30/max_execution_time = 300/' /etc/php/7.4/fpm/php.ini
    sed -i 's/max_input_time = 60/max_input_time = 300/' /etc/php/7.4/fpm/php.ini
    sed -i 's/; date.timezone =/date.timezone = "Asia\/Shanghai"/' /etc/php/7.4/fpm/php.ini    
    # 修改 nginx 配置
    sed -i '/sites-enabled/d' /etc/nginx/nginx.conf
    sed -i '/listen/s/#//' /etc/nginx/conf.d/zabbix.conf
    sed -i '/listen/s/80/8871/' /etc/nginx/conf.d/zabbix.conf
    systemctl restart zabbix-server zabbix-agent nginx php7.4-fpm    
}

function ZeusInstall() {
    ## 安装web
    [ ! -d /opt/zeus ] && mkdir -p /opt/zeus
    cd /opt/zeus
    wget -c https://packages.zmops.cn/zeus-iot/app-1.0.tar.gz -o /dev/null -O - | tar -xz
    wget -c https://packages.zmops.cn/zeus-iot/web-1.0.tar.gz -o /dev/null -O - | tar -xz
    wget -c https://packages.zmops.cn/zeus-iot/zeus-iot.sql
    sudo -u postgres createdb zeus-iot
    cat zeus-iot.sql | sudo -u postgres psql zeus-iot

    # 配置 nginx
    tee /etc/nginx/conf.d/zeus.conf <<EOL 1>/dev/null
server {
    listen       80;


    location / {
      root   /opt/zeus/web;
      index  index.html;
      try_files \$uri \$uri/ /index.html;
    }

    location ^~/api/ {
        client_body_buffer_size 10m;
        proxy_set_header  X-Real-IP        \$remote_addr;
        proxy_set_header  X-Forwarded-For  \$proxy_add_x_forwarded_for;
        proxy_pass http://127.0.0.1:9090/;
    }
    location /zabbix {
            alias /usr/share/zabbix;
            index index.html index.htm index.php;
    }

    location ~ ^/zabbix/.+\.php$ {
            fastcgi_pass   unix:/var/run/php/zabbix.sock;
            fastcgi_index  index.php;
            fastcgi_param  SCRIPT_FILENAME  /usr/share\$fastcgi_script_name;
            include        fastcgi_params;

            fastcgi_param   QUERY_STRING    \$query_string;
            fastcgi_param   REQUEST_METHOD  \$request_method;
            fastcgi_param   CONTENT_TYPE    \$content_type;
            fastcgi_param   CONTENT_LENGTH  \$content_length;

            fastcgi_intercept_errors        on;
            fastcgi_ignore_client_abort     off;
            fastcgi_connect_timeout         60;
            fastcgi_send_timeout            180;
            fastcgi_read_timeout            180;
            fastcgi_buffer_size             128k;
            fastcgi_buffers                 4 256k;
            fastcgi_busy_buffers_size       256k;
            fastcgi_temp_file_write_size    256k;
    }

}
EOL

    # 启动
    systemctl restart nginx 
    cd /opt/zeus/app && ./start.sh

}
InitSystem
AddInstallRepo
PGInstall
ZbxInstall
ZeusInstall