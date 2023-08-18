package com.zmops.iot.web.proxy.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.domain.proxy.Proxy;
import com.zmops.iot.domain.proxy.query.QProxy;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.proxy.dto.ProxyDto;
import com.zmops.iot.web.proxy.dto.ProxyMonitorInfo;
import com.zmops.iot.web.proxy.dto.ZbxProxyInfo;
import com.zmops.iot.web.proxy.dto.ZbxServerInfo;
import com.zmops.iot.web.proxy.dto.param.ProxyParam;
import com.zmops.zeus.driver.service.ZbxProxy;
import io.ebean.DB;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 产品 物模型 服务
 **/
@Service
public class ProxyService {

    @Autowired
    ZbxProxy zbxProxy;

    /**
     * 代理服务分页列表
     *
     * @param proxyParam
     * @return
     */
    public Pager<ProxyDto> getProxyByPage(ProxyParam proxyParam) {
        QProxy qProxy = new QProxy();
        if (ToolUtil.isNotEmpty(proxyParam.getName())) {
            qProxy.name.contains(proxyParam.getName());
        }
        Long tenantId = LoginContextHolder.getContext().getUser().getTenantId();
        if (null != tenantId) {
            qProxy.tenantId.eq(tenantId);
        }
        qProxy.orderBy(" create_time desc");
        List<ProxyDto> productServiceDtoList = qProxy.setFirstRow((proxyParam.getPage() - 1) * proxyParam.getMaxRow())
                .setMaxRows(proxyParam.getMaxRow()).asDto(ProxyDto.class).findList();

        if (ToolUtil.isNotEmpty(productServiceDtoList)) {
            List<String> collect = productServiceDtoList.parallelStream().map(ProxyDto::getZbxId).collect(Collectors.toList());
            String       s       = zbxProxy.get(collect.toString());

            List<ZbxProxyInfo>        zbxProxyInfoList = JSONObject.parseArray(s, ZbxProxyInfo.class);
            Map<String, ZbxProxyInfo> map              = zbxProxyInfoList.parallelStream().collect(Collectors.toMap(ZbxProxyInfo::getProxyid, o -> o));
            productServiceDtoList.forEach(proxyDto -> {
                if (map.get(proxyDto.getZbxId()) != null) {
                    ZbxProxyInfo zbxProxyInfo = map.get(proxyDto.getZbxId());
                    proxyDto.setLastAccess(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(zbxProxyInfo.getLastaccess()), "yyyy-MM-dd HH:mm:ss"));
                    proxyDto.setAutoCompress("1".equals(zbxProxyInfo.getAuto_compress()) ? "是" : "否");
                }
            });
        }

        return new Pager<>(productServiceDtoList, qProxy.findCount());
    }

    /**
     * 代理服务列表
     *
     * @param proxyParam
     * @return
     */
    public List<Proxy> list(ProxyParam proxyParam) {
        QProxy qProxy = new QProxy();
        if (ToolUtil.isNotEmpty(proxyParam.getName())) {
            qProxy.name.contains(proxyParam.getName());
        }
        Long tenantId = LoginContextHolder.getContext().getUser().getTenantId();
        if (null != tenantId) {
            qProxy.tenantId.eq(tenantId);
        }
        qProxy.orderBy(" create_time desc");
        return qProxy.findList();
    }

    /**
     * 代理服务创建
     *
     * @param proxyDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ProxyDto create(ProxyDto proxyDto) {

        Long id = IdUtil.getSnowflake().nextId();

        String result = zbxProxy.proxyCreate(id + "");
        String zbxId  = JSON.parseObject(result, Proxyids.class).getProxyids()[0];

        proxyDto.setId(id);
        proxyDto.setZbxId(zbxId);
        Proxy proxy = new Proxy();
        ToolUtil.copyProperties(proxyDto, proxy);
        DB.save(proxy);

        return proxyDto;
    }

    /**
     * 代理服务修改
     *
     * @param proxyDto
     * @return
     */
    public ProxyDto update(ProxyDto proxyDto) {

        Proxy proxy = new Proxy();
        ToolUtil.copyProperties(proxyDto, proxy);
        DB.update(proxy);

        return proxyDto;
    }

    /**
     * 代理服务删除
     *
     * @param ids
     * @return
     */
    public void delete(List<Long> ids) {
        List<String> zbxIds = new QProxy().select(QProxy.alias().zbxId).id.in(ids).findSingleAttributeList();
        if (ToolUtil.isNotEmpty(zbxIds)) {
            zbxProxy.proxyDelete(zbxIds);
        }

        new QProxy().id.in(ids).delete();

    }

    public Object monitorInfo() {
        List<Proxy> proxyList = list(new ProxyParam());
        if (ToolUtil.isEmpty(proxyList)) {
            return new HashMap<>(0);
        }

        Map<String, ProxyMonitorInfo> proxyMonitorInfoMap = new ConcurrentHashMap<>();

        String host        = "127.0.0.1";
        String zbxApiToken = ForestConfiguration.configuration().getVariableValue("zbxApiToken").toString();


//        String body        = "{\"request\":\"queue.get\",\"type\":\"details\",\"sid\":\"" + zbxApiToken + "\",\"limit\":10}";
//        Forest.post("/zabbix/monitor").host(host).port(12800).contentTypeJson().addBody(body).executeAsMap();
//        body = "{\"request\":\"queue.get\",\"type\":\"overview by proxy\",\"sid\":\"" + zbxApiToken + "\"}";
//        objectObjectMap = Forest.post("/zabbix/monitor").host(host).port(12800).contentTypeJson().addBody(body).executeAsMap();


//        body = "{\"request\":\"queue.get\",\"type\":\"overview\",\"sid\":\"" + zbxApiToken + "\"}";
//        objectObjectMap = Forest.post("/zabbix/monitor").host(host).port(12800).contentTypeJson().addBody(body).executeAsMap();

        ForestRequest<?>    forestRequest   = Forest.post("/zabbix/monitor").host(host).port(12800).contentTypeJson();
        String              body            = "{\"request\":\"status.get\",\"type\":\"full\",\"sid\":\"" + zbxApiToken + "\"}";
        Map<Object, Object> objectObjectMap = forestRequest.addBody(body).executeAsMap();
        formatStr(objectObjectMap.get("data").toString(), proxyMonitorInfoMap, proxyList);


//        body = "{\"request\":\"status.get\",\"type\":\"ping\",\"sid\":\"" + zbxApiToken + "\"}";
//        objectObjectMap = Forest.post("/zabbix/monitor").host(host).port(12800).contentTypeJson().addBody(body).executeAsMap();

        return proxyMonitorInfoMap.values();
    }

    public void formatStr(String data, Map<String, ProxyMonitorInfo> proxyMonitorInfoMap, List<Proxy> proxyList) {
        if (ToolUtil.isEmpty(data)) {
            return;
        }
        proxyList.forEach(proxy -> {
            ProxyMonitorInfo proxyMonitorInfo = new ProxyMonitorInfo();
            proxyMonitorInfo.setProxyId(proxy.getZbxId());
            proxyMonitorInfo.setProxyName(proxy.getName());
            proxyMonitorInfoMap.put(proxy.getZbxId(), proxyMonitorInfo);
        });

        formatHosts(data, proxyMonitorInfoMap);
        formatItems(data, proxyMonitorInfoMap);
//        formatTriggers(data, proxyMonitorInfoMap);
        formatNvps(data, proxyMonitorInfoMap);
    }

    private void formatNvps(String data, Map<String, ProxyMonitorInfo> proxyMonitorInfoMap) {
        List<ZbxServerInfo> stats = JSONObject.parseArray(JSONObject.parseObject(data).getString("required performance"), ZbxServerInfo.class);

        for (ZbxServerInfo performance : stats) {
            String proxyid = performance.getAttributes().getProxyid();
            if ("0".equals(proxyid)) {
                continue;
            }

            proxyMonitorInfoMap.get(proxyid).setNvps(String.format("%.2f", Float.parseFloat(performance.getCount())));

        }
    }

//    private void formatTriggers(String data, Map<String, ProxyMonitorInfo> proxyMonitorInfoMap) {
//        List<ZbxServerInfo> stats = JSONObject.parseArray(JSONObject.parseObject(data).getString("trigger stats"), ZbxServerInfo.class);
//
//        for (ZbxServerInfo triggerStat : stats) {
//            String proxyid = triggerStat.getAttributes().getProxyid();
//            if ("0".equals(proxyid)) {
//                continue;
//            }
//            if (triggerStat.getAttributes().getStatus() == 0) {
//                proxyMonitorInfoMap.get(proxyid).setTriggerStatusEnable(triggerStat.getCount());
//            } else {
//                proxyMonitorInfoMap.get(proxyid).setTriggerStatusDisable(triggerStat.getCount());
//            }
//        }
//    }

    private void formatItems(String data, Map<String, ProxyMonitorInfo> proxyMonitorInfoMap) {
        List<ZbxServerInfo> stats = JSONObject.parseArray(JSONObject.parseObject(data).getString("item stats"), ZbxServerInfo.class);

        for (ZbxServerInfo itemsStat : stats) {
            String proxyid = itemsStat.getAttributes().getProxyid();
            if ("0".equals(proxyid)) {
                continue;
            }
            if (itemsStat.getAttributes().getStatus() == 0) {
                if (itemsStat.getAttributes().getState() == 0) {
                    proxyMonitorInfoMap.get(proxyid).setItemStatusEnable(Integer.parseInt(itemsStat.getCount()));
                } else {
                    proxyMonitorInfoMap.get(proxyid).setItemStatusUnSupport(Integer.parseInt(itemsStat.getCount()));
                }
            } else {
                proxyMonitorInfoMap.get(proxyid).setItemStatusDisable(Integer.parseInt(itemsStat.getCount()));
            }
        }
    }

    public void formatHosts(String data, Map<String, ProxyMonitorInfo> proxyMonitorInfoMap) {

        List<ZbxServerInfo> hostStats = JSONObject.parseArray(JSONObject.parseObject(data).getString("host stats"), ZbxServerInfo.class);

        for (ZbxServerInfo hostStat : hostStats) {
            String proxyid = hostStat.getAttributes().getProxyid();
            if ("0".equals(proxyid)) {
                continue;
            }
            if (hostStat.getAttributes().getStatus() == 0) {
                proxyMonitorInfoMap.get(proxyid).setHostStatusEnable(Integer.parseInt(hostStat.getCount()));
            } else {
                proxyMonitorInfoMap.get(proxyid).setHostStatusDisable(Integer.parseInt(hostStat.getCount()));
            }
        }
    }


    @Data
    static class Proxyids {
        private String[] proxyids;
    }
}
