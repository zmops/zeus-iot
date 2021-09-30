#!/usr/bin/env bash


ROOT_UID=0
release=Centos
basename=$(pwd)
zabbixsrc=$basename/zabbix-5.4.3
ZABBIX_HOME=/opt/zeus/zabbix
PHP_CONF=/etc/opt/rh/rh-php73
sqldir=$basename/zabbix-5.4.3/database/postgresql
PGDATA=/opt/zeus/pgdata

function logprint() {
        if [ $? != 0 ]; then
                echo -e "\033[31m Error: $1 \033[0m"
                exit
        fi
}

# 安装前准备
## 系统环境检测
if [ "$(uname)" != Linux ]; then
        echo "Error: 该脚本只适用 Linux 系统"
        exit
fi

if [ "$UID" -ne "$ROOT_UID" ]; then
        echo -e "Error: 请使用root账户执行安装"
        exit
fi
### 操作系统
if [ ! -f /etc/redhat-release ]; then
        if [[ "$(cat /etc/issue)" =~ ^Ubuntu* ]]; then
                release=Ubuntu
        fi
fi
### 网络
if ! ping -c 3 mirrors.tuna.tsinghua.edu.cn &>/dev/null; then
        echo "Error: 无法访问外网 。。。"
        exit
fi

### cpu、mem、disk
cores=$(grep </proc/cpuinfo -c "processor")
memstotal=$(grep </proc/meminfo "MemTotal" | awk '{printf("%.f\n",$2/1024/1024)}')
disks=$(df -T | awk '/(xfs|ext4|ext3)/{if($3/1024/1024 > 10)printf("%s\t%d\n",$7,$3/1024/1024)}' | grep -v -c "/boot")

if [ "$cores" -lt 0 ] || [ "$memstotal" -lt 0 ] || [ "$disks" -eq 0 ]; then
        echo "Error: 要求系统最低配置为 CPU 2核 内存 4GB 存储空间 100G"
        exit 
fi

## 系统环境初始化
function InitSystem() {
        echo -e -n "\033[32mStep1: 初始化系统安装环境...  \033[0m"
        if ! hostnamectl set-hostname zeus-server; then
                echo "Error: 主机名修改失败"
                exit
        fi

        ### 修改时区
        if ! ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime; then
                echo "Error: 时区修改失败"
                exit 0
        fi

        ### 关闭Selinux
        setenforce 0 || true
        sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config

        ### 加强对抗SYN Flood的能力
        echo "net.ipv4.tcp_syncookies = 1" >>/etc/sysctl.conf

        ### linux文件打开数量限制
        ###/etc/security/limits.conf & /etc/rc.local
        echo "* soft nofile 65535" >>/etc/security/limits.conf
        echo "* hard nofile 65535 " >>/etc/security/limits.conf
        echo "ulimit -SHn 65535" >>/etc/rc.local

        ### 添加用户
        groupadd --system zeus || true
        useradd --system -g zeus zeus || true

        ### 添加安装目录
        [ ! -d /opt/zeus ] && mkdir -p /opt/zeus
        echo -e "\033[32m  [ OK ] \033[0m"
}

# 开始安装
## 配置 YUM 源
function AddInstallRepo() {

        echo -e -n "\033[32mStep2: 配置安装 YUM 源 。。。  \033[0m"
        ### 备份原有yum
        mv /etc/yum.repos.d/* /tmp/
        ### 基础 YUM 源
        ### Postgresql && php
        tee /etc/yum.repos.d/Centos-Base.repo <<EOL &>/dev/null
[base]
name=CentOS-\$releasever - Base
baseurl=http://mirrors.tuna.tsinghua.edu.cn/centos/\$releasever/os/\$basearch/
gpgcheck=0

[epel]
name=Extra Packages for Enterprise Linux 7 - \$basearch
baseurl=https://mirrors.tuna.tsinghua.edu.cn/epel/7/\$basearch
failovermethod=priority
enabled=1
gpgcheck=0

[centos-sclo-rh]
name=CentOS-7 - SCLo rh
baseurl=http://mirrors.tuna.tsinghua.edu.cn/centos/7/sclo/\$basearch/rh/
gpgcheck=0
enabled=1

[pgdg13]
name=PostgreSQL 13 for RHEL/CentOS \$releasever - \$basearch
baseurl=https://mirrors.tuna.tsinghua.edu.cn/postgresql/repos/yum/13/redhat/rhel-\$releasever-\$basearch
enabled=1
gpgcheck=0

EOL

        yum clean all 1>/dev/null && yum makecache 1>/dev/null
        yum -y install java-1.8.0-openjdk expect 1> /dev/null
        logprint "yum源配置失败"
        echo -e "\033[32m  [ OK ] \033[0m"
}

## 安装 PostgreSQL
function PGInstall() {
        echo -e -n "\033[32mStep3: 安装 PostgreSQL....  \033[0m"
        yum -y install postgresql13.x86_64 \
                postgresql13-devel.x86_64 \
                postgresql13-plpython3.x86_64 1>/dev/null

        logprint "yum安装postgresql失败"

        ### 创建 PostgreSQL 用户
        ### 修改启动文件

        [ ! -d $PGDATA ] && mkdir -p $PGDATA
        if ! chown postgres. $PGDATA; then
                echo "修改PGDATA用户失败"
                exit
        fi

        startfile=/usr/lib/systemd/system/postgresql-13.service
        #sed -i 's/\(^User\=\).*/\1zeus/g' $startfile
        #sed -i 's/\(^Group\=\).*/\1zeus/g' $startfile
        sed -i 's/\(^Environment\=PGDATA\=\).*/\1\/opt\/zeus\/pgdata/g' $startfile
        ### 初始化数据库
        /usr/pgsql-13/bin/postgresql-13-setup initdb 1>/dev/null
        logprint "初始化PG错误"

#        echo "shared_preload_libraries = 'timescaledb'" >>/opt/zeus/pgdata/postgresql.conf
        ### 启动数据库
        systemctl enable postgresql-13 &>/dev/null
        systemctl start postgresql-13
        ### 修改数据库管理员密码
        cd /tmp || exit
        sudo -u postgres /usr/pgsql-13/bin/psql -c "ALTER USER postgres WITH PASSWORD 'postgres';" 1> /dev/null
        echo -e "\033[32m  [ OK ] \033[0m"
}

## 编译安装 zabbix 5.4
function ZbxInstall() {
        echo -e -n "\033[32mStep4: 编译安装 zabbix 。。。  \033[0m"
        ### 安装编译依赖
        cd "$basename" || exit
        yum -y install vim \
                wget \
                gcc \
                gcc-c++ \
                net-snmp \
                net-snmp-libs \
                net-snmp-utils \
                net-snmp-devel \
                libssh2-devel \
                OpenIPMI-devel \
                libevent-devel \
                libcurl-devel \
                libxml2 \
                libxml2-devel 1>/dev/null

        ### 创建 zabbix 用户
        groupadd --system zabbix || true
        useradd --system -g zabbix -d /opt/zeus/zabbix -s /sbin/nologin -c "Zabbix Monitoring System" zabbix || true

        wget -c https://cdn.zabbix.com/zabbix/sources/stable/5.4/zabbix-5.4.3.tar.gz -o /dev/null -O - | tar -xz
        logprint "下载zabbix源码失败，请检查网络。。。"

        cd "$zabbixsrc" && ./configure --prefix=$ZABBIX_HOME \
                --enable-server \
                --enable-agent \
                --with-postgresql=/usr/pgsql-13/bin/pg_config \
                --with-net-snmp \
                --with-libcurl \
                --with-libxml2 \
                --with-openipmi \
                --with-openssl \
                --with-ssh2 1>/dev/null
        logprint "zabbix编译异常"

        make install 1>/dev/null
        logprint "zabbix编译异常"
        ### 前端内容部署
        mv ui $ZABBIX_HOME/zabbix && chown zeus. $ZABBIX_HOME/zabbix -R
        mv $ZABBIX_HOME/zabbix/conf/zabbix.conf.php.example $ZABBIX_HOME/zabbix/conf/zabbix.conf.php
        sed -i "s/\($DB\['PASSWORD'\]\s*=\).*/\1 'zabbix';/g" $ZABBIX_HOME/zabbix/conf/zabbix.conf.php
        sed -i "s/\($DB\['TYPE'\]\s*=\).*/\1 \'POSTGRESQL\';/g" $ZABBIX_HOME/zabbix/conf/zabbix.conf.php
        echo -e "\033[32m  [ OK ] \033[0m"
        ### 数据初始化
        echo -e -n "\033[32mStep5: 初始化 zabbix 数据库 。。。  \033[0m"
        cd /tmp/ || exit
        sudo -u postgres createuser zabbix
        sudo -u postgres /usr/pgsql-13/bin/psql -c "ALTER USER zabbix WITH PASSWORD 'zabbix';" 1> /dev/null
        sudo -u postgres createdb -O zabbix -E Unicode -T template0 zabbix
        cat $zabbixsrc/database/postgresql/schema.sql | sudo -u zabbix psql zabbix 1>/dev/null
        cat $zabbixsrc/database/postgresql/images.sql | sudo -u zabbix psql zabbix 1>/dev/null
        cat $zabbixsrc/database/postgresql/data.sql | sudo -u zabbix psql zabbix 1>/dev/null
#        echo "CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;" | sudo -u postgres psql zabbix &>/dev/null
#        cat $zabbixsrc/database/postgresql/timescaledb.sql | sudo -u zabbix psql zabbix 1>/dev/null
#        
        echo -e "\033[32m  [ OK ] \033[0m"
        cd "$basename" || exit
        ### 配置zabbix配置文件
        echo -e -n "\033[32mStep6: 启动 zabbix ....  \033[0m"
        sed -i 's/^# DBPassword=/DBPassword=zabbix/g' $ZABBIX_HOME/etc/zabbix_server.conf
        ### 配置启动文件
        tee /usr/lib/systemd/system/zabbix-server.service <<EOL 1>/dev/null
[Unit]
Description=Zabbix Server

After=network.target
After=postgresql-13.service

[Service]
Environment="CONFFILE=$ZABBIX_HOME/etc/zabbix_server.conf"
EnvironmentFile=-/etc/default/zabbix-server
Type=forking
Restart=on-failure
PIDFile=/tmp/zabbix_server.pid
KillMode=control-group
ExecStart=$ZABBIX_HOME/sbin/zabbix_server -c \$CONFFILE
ExecStop=/bin/kill -SIGTERM \$MAINPID
RestartSec=10s
TimeoutSec=0

[Install]
WantedBy=multi-user.target
EOL

        tee /usr/lib/systemd/system/zabbix-agent.service <<EOL 1>/dev/null
[Unit]
Description=Zabbix Agent
After=network.target

[Service]
Environment="CONFFILE=$ZABBIX_HOME/etc/zabbix_agentd.conf"
EnvironmentFile=-/etc/default/zabbix-agent
Type=forking
Restart=on-failure
PIDFile=/tmp/zabbix_agentd.pid
KillMode=control-group
ExecStart=$ZABBIX_HOME/sbin/zabbix_agentd -c \$CONFFILE
ExecStop=/bin/kill -SIGTERM \$MAINPID
RestartSec=10s
User=zabbix
Group=zabbix

[Install]
WantedBy=multi-user.target
EOL

        systemctl daemon-reload
        systemctl enable zabbix-server &> /dev/null
        systemctl enable zabbix-agent &> /dev/null
        systemctl start zabbix-server
        systemctl start zabbix-agent
        echo -e "\033[32m  [ OK ] \033[0m"
}
## 安装 php
function PHPInstall() {
        echo -e -n "\033[32mStep7: 安装 zabbix-web ....  \033[0m"
        yum -y install rh-php73.x86_64 \
                rh-php73-php-fpm.x86_64 \
                rh-php73-php-bcmath.x86_64 \
                rh-php73-php-gd.x86_64 \
                rh-php73-php-mbstring.x86_64 \
                rh-php73-php-ldap.x86_64 \
                rh-php73-php-pgsql.x86_64 1>/dev/null

        ### php配置文件修改
        sed -i 's/post_max_size = 8M/post_max_size = 16M/' $PHP_CONF/php.ini
        sed -i 's/upload_max_filesize = 2M/upload_max_filesize = 20M/' $PHP_CONF/php.ini
        sed -i 's/max_execution_time = 30/max_execution_time = 300/' $PHP_CONF/php.ini
        sed -i 's/max_input_time = 60/max_input_time = 300/' $PHP_CONF/php.ini
        sed -i 's/; date.timezone =/date.timezone = "Asia\/Shanghai"/' $PHP_CONF/php.ini
        sed -i 's/user = apache/user = zeus/g' $PHP_CONF/php-fpm.d/www.conf
        sed -i 's/group = apache/group = zeus/g' $PHP_CONF/php-fpm.d/www.conf
        sed -i 's/;listen.owner = nobody/listen.owner = zeus/g' $PHP_CONF/php-fpm.d/www.conf
        sed -i 's/;listen.group = nobody/listen.group = zeus/g' $PHP_CONF/php-fpm.d/www.conf
        sed -i 's/\(^listen =\).*/\1\/var\/run\/php-fpm.sock/g' $PHP_CONF/php-fpm.d/www.conf
        echo -e "\033[32m  [ OK ] \033[0m"
}

function WebInstall() {
        echo -e -n "\033[32mStep8: 启动 zabbix-web ....  \033[0m"
        ## 安装 nginx
        yum -y install nginx 1>/dev/null


        cd /opt/zeus
        
        ## 编辑 nginx 配置文件
        tee /etc/nginx/conf.d/zabbix.conf <<EOL 1>/dev/null
server {
    listen       80;

    location / {
        rewrite ^/(.*) http://$host:9090/$1 permanent;
    }

    location /zabbix {
        alias $ZABBIX_HOME/zabbix;
        index index.html index.htm index.php;
    }

    location ~ ^/zabbix/.+\.php$ {
            fastcgi_pass   unix:/var/run/php-fpm.sock;
            fastcgi_index  index.php;
            fastcgi_param  SCRIPT_FILENAME  $ZABBIX_HOME\$fastcgi_script_name;
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

        ## 修改 nginx 配置用户
        sed -i 's/user nginx;/user zeus;/g' /etc/nginx/nginx.conf
        systemctl enable rh-php73-php-fpm &> /dev/null
        systemctl enable nginx &> /dev/null
        systemctl start rh-php73-php-fpm
        systemctl start nginx
        echo -e "\033[32m  [ OK ] \033[0m"
}



function gettoken(){
        ## 获取 API 永久 token
        local zabbix_api_url=http://127.0.0.1/zabbix/api_jsonrpc.php
        local data='{"jsonrpc": "2.0","method": "user.login","params":{"user":"Admin","password":"zabbix"},"id":1,"auth":null}'
        local res=`eval exec "curl -d '$data' -H 'Content-Type: application/json' -X POST -s $zabbix_api_url"`
        local auth=`echo $res | python -c 'import sys, json; print(json.load(sys.stdin)["result"])'`
        local data='{"jsonrpc": "2.0","method": "token.create","params":{"name":"zeus","userid":"1"},"id":1,"auth":"'${auth}'"}'
        local res=`eval exec "curl -d '$data' -H 'Content-Type: application/json' -X POST -s $zabbix_api_url"`
        local tokenid=`echo $res | python -c 'import sys, json; print(json.load(sys.stdin)["result"]["tokenids"][0])'`
        local data='{"jsonrpc": "2.0","method": "token.generate","params":["'${tokenid}'"],"id":1,"auth":"'${auth}'"}'
        local res=`eval exec "curl -d '$data' -H 'Content-Type: application/json' -X POST -s $zabbix_api_url"`
        token=`echo $res | python -c 'import sys, json; print(json.load(sys.stdin)["result"][0]["token"])'`     
}


function taosinstall() {
        ### taos 数据安装
        echo -e -n "\033[32mStep9: 安装 taos 数据库。。。  \033[0m"
        expect << EOF 1> /dev/null
          spawn rpm -ivh https://www.taosdata.com/assets-download/TDengine-server-2.2.0.2-Linux-x64.rpm
          expect {
            "*one:" { send "\n";exp_continue}
            "*skip:" { send "\n" }
          }
EOF
        ## 启动taos
        systemctl enable taosd &> /dev/null
        systemctl start taosd 

        echo -e "\033[32m  [ OK ] \033[0m"
}


function ZeusInstall() {
        echo -e -n "\033[32mStep11: 安装 Zeus-IoT 服务。。。  \033[0m"
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
        ./zeus-iot-bin/bin/startup.sh 1> /dev/null
        echo -e "\033[32m  [ OK ] \033[0m"
}


##
InitSystem
AddInstallRepo
PGInstall
ZbxInstall
PHPInstall
WebInstall
taosinstall
ZeusInstall
# 安装结束
