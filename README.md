<p align="center">
<img src="docs/images/zeus-iot-logo.png" alt="banner" width="200px">
</p>

<p align="center">
<b>Zeus IoT is the world's first open source IoT <i>distributed collection platform</i> based on Zabbix</b>
</p>

----

[![GitHub stars](https://img.shields.io/github/stars/zmops/zeus-iot.svg?label=Stars&logo=github)](https://github.com/zmops/zeus-iot)
[![GitHub issues](https://img.shields.io/github/issues/zmops/zeus-iot?label=Issuess&logo=github)](https://github.com/zmops/zeus-iot)
[![GitHub forks](https://img.shields.io/github/forks/zmops/zeus-iot?label=Forks&logo=github)](https://github.com/zmops/zeus-iot)
![Version](https://img.shields.io/badge/version-1.0.0--RELEASE-brightgreen)
[![QQ群736541577](https://img.shields.io/badge/QQ群-736541577-brightgreen)](https://qm.qq.com/cgi-bin/qm/qr?k=CcWBdkXjkgt99bBu5d_-1TeS36DhCkU4&jump_from=webapi)

## Abstract
[Zeus IoT](https://www.zmops.com/) is a **distributed IoT collection, analysis, and storage platform**,It is the world's first IoT open source platform based on zabbix secondary development, all this relies on a group of engineers with rich experience in zabbix development.It is hoped that through the community’s open source ecology, continuous improvement and continuous updates will make some contributions to the development of the Internet of Things industry.

The following screenshots give a close insight into Zeus IoT.
<table>
  <tr>
      <td width="50%" align="center"><b>Home Screen</b></td>
      <td width="50%" align="center"><b>Device Resources</b></td>
  </tr>
  <tr>
     <td><img src="docs/images/snapshot_1.jpg"/></td>
     <td><img src="docs/images/snapshot_3.jpg"/></td>
  </tr>
  <tr>
      <td width="50%" align="center"><b>Realtime Data</b></td>
      <td width="50%" align="center"><b>System Users</b></td>
  </tr>
  <tr>
     <td><img src="docs/images/snapshot_2.jpg"/></td>
     <td><img src="docs/images/snapshot_4.jpg"/></td>
  </tr>
</table>

## Technology stack
- Basic components: Zabbix 5.4+
- Database: Postgresql 12+
- Webapp: SpringBoot 2  +  [Ebean](https://ebean.io/)  +  Vue Element + Socket.IO
- IoT Server:  Apache Camel 2.2 + Modular design
- Visualization: Grafana 8.0+

## Demo Environment

Using the account `Admin / zeus123` to log in the [demo environment](https://zeusdemo.zmops.cn/). Please note the account is granted view access. 