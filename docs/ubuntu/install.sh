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

if [ "$UID" -ne 0 ]; then
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
    echo -e -n "\033[32mStep1: 初始化系统安装环境。。。  \033[0m"
    ## 修改主机名
    if ! hostnamectl set-hostname zeus-server; then
        echo "主机名修改失败"
        exit
    fi

    ## 关闭 IPV6 监听

    echo " ">>/etc/sysctl.conf
    echo "# made for disabled IPv6 in $(date +%F)">>/etc/sysctl.conf
    echo 'net.ipv6.conf.all.disable_ipv6 = 1'>>/etc/sysctl.conf
    echo 'net.ipv6.conf.default.disable_ipv6 = 1'>>/etc/sysctl.conf
    echo 'net.ipv6.conf.lo.disable_ipv6 = 1'>>/etc/sysctl.conf
    sysctl -p

    ## 修改时区
    if ! ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime; then
        echo "时区修改失败"
        exit 0
    fi
    ## 更新下载源
    cp /etc/apt/sources.list /etc/apt/sources.listbak
    sed -i 's%\(^deb http\{0,1\}\:\/\/\).*\(/ubuntu.*\)%\1mirrors.tuna.tsinghua.edu.cn\2%g' /etc/apt/sources.list
    echo -e "\033[32m  [ OK ] \033[0m"
}

function AddInstallRepo() {
    echo -e -n "\033[32mStep2: 配置安装 YUM 源 。。。  \033[0m"
    ## 安装PGsql源
    echo "deb https://mirrors.tuna.tsinghua.edu.cn/postgresql/repos/apt/ $(lsb_release -c -s)-pgdg main" | sudo tee /etc/apt/sources.list.d/pgdg.list &> /dev/null
    wget --quiet -O - https://mirrors.tuna.tsinghua.edu.cn/postgresql/repos/apt/ACCC4CF8.asc | sudo apt-key add -
    #sudo sh -c "echo 'deb https://packagecloud.io/timescale/timescaledb/ubuntu/ $(lsb_release -c -s) main' > /etc/apt/sources.list.d/timescaledb.list"
    #wget --quiet -O - https://packagecloud.io/timescale/timescaledb/gpgkey | sudo apt-key add -
    ## 安装zabbix 5.4 源
    wget -q https://repo.zabbix.com/zabbix/5.4/ubuntu/pool/main/z/zabbix-release/zabbix-release_5.4-1+ubuntu$(lsb_release -r -s)_all.deb
    dpkg -i zabbix-release_5.4-1+ubuntu$(lsb_release -r -s)_all.deb 1> /dev/null
    apt update 1> /dev/null && apt install -y openjdk-8-jdk expect 1> /dev/null
    echo -e "\033[32m  [ OK ] \033[0m"
}

function PGInstall() {
    echo -e -n "\033[32mStep3: 安装 PostgreSQL....  \033[0m"
    # PG 安装
    apt install postgresql-13 -y 1> /dev/null
    #echo "shared_preload_libraries = 'timescaledb'" >> /etc/postgresql/13/main/postgresql.conf
    cd /tmp || exit
    sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'postgres';" 2> /tmp/pg.log
    systemctl restart postgresql
    echo -e "\033[32m  [ OK ] \033[0m"
}

function ZbxInstall() {
    echo -e -n "\033[32mStep4: 编译安装 zabbix 。。。  \033[0m" 
    # zabbix 安装
    apt install zabbix-server-pgsql zabbix-frontend-php php-pgsql zabbix-nginx-conf zabbix-sql-scripts zabbix-agent -y 1> /dev/null

    PHPCONF=/etc/php/$(ls /etc/php/)/fpm/php.ini

    # 初始化 zabbix 配置
    cd /tmp || exit
    sudo -u postgres createuser zabbix
    sudo -u postgres psql -c "ALTER USER zabbix WITH PASSWORD 'zabbix';"
    sudo -u postgres createdb -O zabbix zabbix
    zcat /usr/share/doc/zabbix-sql-scripts/postgresql/create.sql.gz | sudo -u zabbix psql zabbix 1> /dev/null
    #echo "CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;" | sudo -u postgres psql zabbix
    #cat /usr/share/doc/zabbix-sql-scripts/postgresql/timescaledb.sql | sudo -u zabbix psql zabbix 1>/dev/null

    sed -i 's/^# DBPassword=/DBPassword=zabbix/g' /etc/zabbix/zabbix_server.conf
    mv /usr/share/zabbix/conf/zabbix.conf.php.example /usr/share/zabbix/conf/zabbix.conf.php
    sed -i "s/\($DB\['PASSWORD'\]\s*=\).*/\1 'zabbix';/g" /usr/share/zabbix/conf/zabbix.conf.php
    sed -i "s/\($DB\['TYPE'\]\s*=\).*/\1 \'POSTGRESQL\';/g" /usr/share/zabbix/conf/zabbix.conf.php
    # 修改 php 配置
    sed -i 's/post_max_size = 8M/post_max_size = 16M/' $PHPCONF
    sed -i 's/upload_max_filesize = 2M/upload_max_filesize = 20M/' $PHPCONF
    sed -i 's/max_execution_time = 30/max_execution_time = 300/' $PHPCONF
    sed -i 's/max_input_time = 60/max_input_time = 300/' $PHPCONF
    sed -i 's/; date.timezone =/date.timezone = "Asia\/Shanghai"/' $PHPCONF    
    # 修改 nginx 配置
    sed -i '/sites-enabled/d' /etc/nginx/nginx.conf
    sed -i '/listen/s/#//' /etc/nginx/conf.d/zabbix.conf
    sed -i '/listen/s/80/8871/' /etc/nginx/conf.d/zabbix.conf

    # 配置 zabbix web
    
    tee /etc/nginx/conf.d/zeus.conf <<EOL 1>/dev/null
server {
    listen       80;

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

    systemctl restart zabbix-server zabbix-agent nginx php$(ls /etc/php/)-fpm  
    echo -e "\033[32m  [ OK ] \033[0m"  
}


function gettoken(){
        ## 获取 API 永久 token
        local zabbix_api_url=http://127.0.0.1/zabbix/api_jsonrpc.php
        local data='{"jsonrpc": "2.0","method": "user.login","params":{"user":"Admin","password":"zabbix"},"id":1,"auth":null}'
        local res=`eval exec "curl -d '$data' -H 'Content-Type: application/json' -X POST -s $zabbix_api_url"`
        local auth=`echo $res | python3 -c 'import sys, json; print(json.load(sys.stdin)["result"])'`
        local data='{"jsonrpc": "2.0","method": "token.create","params":{"name":"zeus","userid":"1"},"id":1,"auth":"'${auth}'"}'
        local res=`eval exec "curl -d '$data' -H 'Content-Type: application/json' -X POST -s $zabbix_api_url"`
        local tokenid=`echo $res | python3 -c 'import sys, json; print(json.load(sys.stdin)["result"]["tokenids"][0])'`
        local data='{"jsonrpc": "2.0","method": "token.generate","params":["'${tokenid}'"],"id":1,"auth":"'${auth}'"}'
        local res=`eval exec "curl -d '$data' -H 'Content-Type: application/json' -X POST -s $zabbix_api_url"`
        token=`echo $res | python3 -c 'import sys, json; print(json.load(sys.stdin)["result"][0]["token"])'`     
}


function taosinstall() {
        ### taos 数据安装
        echo -e -n "\033[32mStep5: 安装 taos 数据库。。。  \033[0m"
        wget https://www.taosdata.com/assets-download/TDengine-server-2.2.0.2-Linux-x64.deb
        expect << EOF 1> /dev/null
          spawn dpkg -i TDengine-server-2.2.0.2-Linux-x64.deb
          expect {
            "*one:" { send "\n";exp_continue}
            "*skip:" { send "\n" }
          }
EOF
        systemctl enable taosd
        systemctl start taosd
        echo -e "\033[32m  [ OK ] \033[0m"
}


function ZeusInstall() {
        echo -e -n "\033[32mStep6: 安装 Zeus-IoT 服务。。。  \033[0m"
        [ ! -d /opt/zeus ] && mkdir -p /opt/zeus
        cd /opt/zeus || exit
        ## 创建taos 数据库
        taos -s "create database zeus_data" 1> /dev/null
        wget -c https://packages.zmops.cn/zeus-iot/zeus-iot-bin.tar.gz -o /dev/null -O - | tar -xz
        gettoken
        ## 数据库导入
        sudo -u postgres createdb -E Unicode -T template0 zeus-iot
        cat ./zeus-iot-bin/bin/sql/zeus-iot.sql | sudo -u postgres psql zeus-iot &>/dev/null
        logprint "文件未找到"
        sed -i "s%\(zbxApiToken: \).*%\1$token%" ./zeus-iot-bin/webapp/webapp.yml
        #sed -i '19i export ZEUS_DB_PASSWORD=zeusiot' ./zeus-iot-bin/bin/startup.sh
        ./zeus-iot-bin/bin/startup.sh 1> /dev/null
        echo -e "\033[32m  [ OK ] \033[0m"
}




InitSystem
AddInstallRepo
PGInstall
ZbxInstall
taosinstall
#ZeusInstall
echo "zabbix 部分已安装成功，zeus iot 可以参照 www.zmops.com 官方文档自定义安装。"
