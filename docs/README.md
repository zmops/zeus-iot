### Zeus IOT Document

#### 自定义安装

```
⚠️ : 自定义安装是在本身已有 zabbix 或 分开部署zabbix 和 zeus。
```

- **zabbix 安装可参照[zabbix官网](https://www.zabbix.com/download) 。或者[快速安装](../README.md)。这里就不做详细介绍。**

- **zeus iot 安装**

  - **前端 UI 部署**

    可采用 nginx 做为web服务器来部署 ui。

    - Ubuntu 安装 nginx

      ```shell
      apt install nginx -y
      ```

    - Centos/RedHat 安装 nginx

      ```shell
      yum install nginx -y 
      ```

    - nginx 配置文件

      ```shell
      tee /etc/nginx/conf.d/zabbix.conf <<EOL
      server {
          listen       80;
          location / {
            root   /opt/zeus/web; # 前端 UI 根目录 
            index  index.html;
            try_files \$uri \$uri/ /index.html;
          }
      
          location ^~/api/ {
              client_body_buffer_size 10m;
              proxy_set_header  X-Real-IP        \$remote_addr;
              proxy_set_header  X-Forwarded-For  \$proxy_add_x_forwarded_for;
              proxy_pass http://127.0.0.1:9090/; # zeus-iot server 接口地址
          }
      EOL
      ```

    - 获取 zeus-ui

      - 直接下载[release]()包

      - 从源码编译

        ```shell
        git clone http://code.zmops.cn/zeus-iot/zeus-iot-ui/zeus-iot-ui.git
        cd zeus-iot-ui/ && npm install && npm run build
        mv dist /opt/zeus/web
        ```

        

  - **zeus-iot 安装**

    > zeus server 是以 jar 包的形式部署在服务器上。

    需要安装 JDK 1.8 以上开发工具包。

    - Ubuntu 安装 JDK 1.8

      ```shell
      apt install openjdk-8-jdk -y
      ```

    - Centos/Redhat 安装 JDK 1.8

      ```shell
      yum install java-1.8.0-openjdk.x86_64 -y
      ```

    - 获取 zeus-iot.jar 包

      - 直接下载 release 包

      - 从源码编译

        ```shell
        git clone https://github.com/zmops/zeus-iot.git
        cd zeus-iot/ && mvn clean package -U -Dmaven.test.skip=true
        ```

      - 启动服务

        ```shell
        export ZEUS_DB_HOST=127.0.0.1								#zeus 数据库地址，默认: 127.0.0.1
        export ZEUS_DB_PORT=5432                    #zeus 数据库端口，默认: 5432
        export ZEUS_DB_USERNAME=postgres            #zeus 数据库用户名，默认: postgres
        export ZEUS_DB_PASSWORD=postgres						#zeus 数据库密码，默认: postgres
        export ZEUS_ZABBIX_HOST=127.0.0.1           #zabbix server 地址，默认: 127.0.0.1
        export ZEUS_ZABBIX_API_PORT=80              #zabbix web 端口，默认: 80
        export ZEUS_ZABBIX_PORT=10051               #zabbix server 端口，默认: 10051
        export ZEUS_CALLBACK_HOST=127.0.0.1         #zeus-iot 地址，默认: 127.0.0.1
        export ZEUS_CALLBACK_PORT=9090              #zeus-iot 端口，默认: 9090
        ```

        以上环境变量，根据服务架构环境，做相应修改。不设置即使用默认值

        ```shell
        nohup java -jar zeus-iot.jar > /tmp/zeus-iot.log 2>&1 &
        ```

        

