[toc]

# Zeus IoT Document

> Zeus IoT  基于 zabbix-5.4 安装。

## 环境要求

> 目前 ZEUS 支持 Linux x86_64 系统平台。

| 配置规格 |    操作系统    | CPU  | 内存 | 存储  |
| :------: | :------------: | :--: | :--: | :---: |
| 最低配置 | Centos、Ubuntu | 2 核 | 4 GB | 100GB |
| 推荐配置 | Centos、Ubuntu | 4 核 | 8 GB | 500GB |

## 快速安装

> 简化 zabbix 安装部署，方便客户快速使用 Zeus IoT 产品。

系统终端执行以下命令快速安装

- Centos7/Redhat7

  ```shell
  curl -sL https://github.com/zmops/zeus-iot/raw/develop/docs/centos/install.sh | bash -s install
  ```

- Ubuntu 20.04

  ```shell
   curl -sL https://github.com/zmops/zeus-iot/raw/develop/docs/ubuntu/install.sh | bash -s install
  ```

## 自定义安装

> 自定义安装是在本身已有 zabbix 或需要分开部署 zabbix 和 zeus-iot 服务。

### 安装 zabbix 服务

> **zabbix 安装可参照**[zabbix官网](https://www.zabbix.com/download) 。**这里就不做详细介绍**。

:bulb:    **建议使用 PostgreSQL 数据库，因为 zeus-iot-ui 使用的是 PostgreSQL 数据库。**



#### zabbix 系统优化

- **修改 zabbix 时区**
  - 管理员用户登录 zabbix 管理界面进入 "**管理(Administrator)**" ----- "**一般()**" ----- "**Default time zone**" 选择 "(UTC+08:00) Asia/Shanghai"
  - 普通用户登录 zabbix 管理界面进入 "**User settings**" ----- "**Profile**" ----- "**Time zone**" 选择 "(UTC+08:00) Asia/Shanghai"

### 安装 zeus-iot 系统

#### 安装 PostgreSQL 数据库

- Centos/RedHat

  ```shell
  # 安装 PostgreSQL 仓库源:
  sudo yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm
  
  # 安装 PostgreSQL:
  sudo yum install -y postgresql13-server
  
  # 初始化数据库并启动:
  sudo /usr/pgsql-13/bin/postgresql-13-setup initdb
  sudo systemctl enable postgresql-13
  sudo systemctl start postgresql-13
  ```


- Ubuntu
  
  ```shell
  # 安装 PostgreSQL 仓库源:
  sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
  wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
  
  # 安装 PostgreSQL:
  sudo apt-get update && sudo apt-get -y install postgresql
  ```



#### 获取 zeus-iot 程序包

- 直接下载 release 包

  ```shell
wget -c https://packages.zmops.cn/zeus-iot/zeus-iot-bin.tar.gz
  ```

- 从源码编译

  ```shell
  git clone https://github.com/zmops/zeus-iot.git
  cd zeus-iot && git submodule update --init --recursive
  mvn clean package -U -Dmaven.test.skip=true
  ```

#### 初始化系统服务

1、 创建数据库

```shell
sudo -u postgres createdb -E Unicode -T template0 zeus-iot
```

2、 导入 SQL

```shell
cat zeus-iot.sql | sudo -u postgres psql zeus-iot
```

3、 安装 JDK 驱动

```shell
# Centos/Redhat 安装
yum install java-1.8.0-openjdk.x86_64 -y
# Ubuntu 安装
apt install openjdk-8-jdk -y
```

#### 服务启动配置

> zeus-iot 主要由 zeus-iot-server 和 webapp 两个服务组成，配置文件分别是 ``./zeus-iot-bin/conf/application.yml`` 和 ``./zeus-iot-bin/webapp/webapp.yml``。

##### 配置 zabbix token 

1、登录 zabbix 管理界面进入 "**User settings**" ----- "**API tokens**" ----- "**Create API token**"

2、创建名为 zeus 的 API token, 取消 "**Set expiration date and time**" 的勾选项。确定后会生成 Auth token,点击 "**Copy to clipboard**" 复制token。

3、配置 zeus-iot 连接 zabbix token

    vim /opt/zeus/zeus-iot-bin/webapp/webapp.yml
    
    ...
    forest:
      log-enabled: false
      timeout: 5000
      variables:
        ...
        zbxServerPort: ${ZEUS_ZABBIX_PORT:80}
        zbxApiToken: 4d3ed2be23a3f325d6ccaaaeab76bbdc6a559f3c608e523f9906ea923f7d61c5    # 修改此处token信息
        ...
    ...

##### 其它启动参数配置

- 直接修改配置文件
  
  以修改 zabbix 信息为例：

  ```
    vim /opt/zeus/zeus-iot-bin/webapp/webapp.yml
  
    forest:
      log-enabled: false
      timeout: 5000
      variables:
        ## Zabbix API IP And ServerIp and ServerPort  
        zbxServerIp: ${ZEUS_ZABBIX_HOST:127.0.0.1}                                                       
        zbxServerPort: ${ZEUS_ZABBIX_PORT:80}
        zbxApiToken: 4d3ed2be23a3f325d6ccaaaeab76bbdc6a559f3c608e523f9906ea923f7d61c5
        taosUrl: http://${ZEUS_TAOS_HOST:127.0.0.1}:${ZEUS_TAOS_REST_PORT:6041}/rest/sql/${ZEUS_TAOS_DB:zeus_data}
        taosUser: root
        taosPwd: taosdata
  ```
  
  


- 通过配置环境变量的方式修改(临时修改)

  zeus-iot 支持环境变量配置启动，可以通过 ``export ZEUS_ZABBIX_HOST=127.0.0.1`` 申明环境变量。

#### 启动服务

> zeus-iot 默认需要连接 taos 数据库，所以启动前请先[配置taos数据库](#配置 taos 数据库)

```shell
# 启动 
/opt/zeus/zeus-iot-bin/bin/startup.sh

# 停止
/opt/zeus/zeus-iot-bin/bin/stop.sh
```




配置 taos 数据库
================

安装 taos 数据库
----------------

### 获取 taos 数据库

> 可通过[涛思官网](https://www.taosdata.com/en/getting-started/)获取安装包

:bulb:  zeus-iot 默认使用 **RESTful connector** 连接涛思数据库。

### 安装 taos 数据库

- **Centos/Redhat 安装**

  ```shell
rpm -ivh TDengine-server-2.2.0.2-Linux-x64.rpm
  ```

- **Ubuntu/Debain 安装**

  ```shell
  dpkg -i TDengine-server-2.2.0.2-Linux-x64.deb
  ```

:bulb:  这里只介绍单机版涛思安装部署，安装时是以交互式安装，只需按回车。如要安装集群版涛思需要查阅 [涛思官网文档](https://www.taosdata.com/en/documentation/)

- **启动 taos**

  ```shell
systemctl start taosd
  ```

- **创建 zeus_data 数据库**

  ```shell
  # linux 命令行登录taos 
   taos
  
  # 创建 zeus_data 数据库
  taos> create database zeus_data;
  ```

启用 zabbix 导出功能
---------------------

修改 zabbix-server.conf 配置文件，如下：

- ExportDir=/data/zabbix_history 配置导出目录
- ExportFileSize=1G 配置导出文件的大小
- ExportType=history 配置导出的数据表


创建导出的目录并重启 zabbix-server 使配置生效

```shell
mkdir -p /data/zabbix_history
systemctl restart zabbix-server
```


修改 zeus-iot 数据库连接
--------------------------

```
# 停止 zeus-iot 服务
/opt/zeus/zeus-iot-bin/bin/stop.sh

# 修改数据连接配置如下
vim /opt/zeus/zeus-iot-bin/conf/application.yml
  ...
  # tdengine storage realtime
  storage:
  selector: ${ZS_STORAGE:tdengine}
  tdengine:
    url: ${ZS_STORAGE_TDENGINE_URL:jdbc:TAOS://127.0.0.1:6030/zeus_data} # TDEngine jdbcUrl
    user: ${ZS_STORAGE_TDENGINE_USER:root}
    password: ${ZS_STORAGE_TDENGINE_PASSWORD:taosdata}
  ...

```

## 启动 zeus-iot 服务

```shell
 /opt/zeus/zeus-iot-bin/bin/startup.sh
```

