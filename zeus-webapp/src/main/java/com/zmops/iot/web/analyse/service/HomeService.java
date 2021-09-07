package com.zmops.iot.web.analyse.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.service.ZbxHost;
import com.zmops.zeus.driver.service.ZbxInitService;
import com.zmops.zeus.driver.service.ZbxItem;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 全局概览服务
 **/
@Service
public class HomeService {

    @Autowired
    private ZbxHost zbxHost;

    @Autowired
    private ZbxItem zbxItem;

    @Autowired
    HistoryService historyService;

    @Autowired
    private ZbxInitService zbxInitService;

    @Value("${forest.variables.zbxServerIp}")
    private String zbxServerIp;

    @Value("${forest.variables.zbxServerPort}")
    private String zbxServerPort;

    private static String hostId = "";

    private static Map<String, String> ITEM_Map = new ConcurrentHashMap<>(5);

    private static String COOKIE = "";

    private static LocalDateTime COOKIE_TIME;

    //Zbx 指标取数速率 key
    private static final String KEY = "zabbix[wcache,values";

    /**
     * 服务器取数速率统计
     *
     * @return
     */
    public Map<String, List<LatestDto>> collectonRate() {
        if (ToolUtil.isEmpty(hostId)) {
            if (ToolUtil.isEmpty(getZbxServerId())) {
                return new HashMap<>(0);
            }
        }
        if (ToolUtil.isEmpty(ITEM_Map)) {
            if (ToolUtil.isEmpty(getItemMap())) {
                return new HashMap<>(0);
            }
        }
        List<String> itemIds = new ArrayList<>(ITEM_Map.keySet());
        List<LatestDto> latestDtos = historyService.queryHitoryData(hostId, itemIds, 0, LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.minu(LocalDateTime.now(),
                7, ChronoUnit.DAYS)), LocalDateTimeUtils.getSecondsByTime(LocalDateTime.now()));

        latestDtos.forEach(latestDto -> {
            latestDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(latestDto.getClock()), "yyyy-MM-dd HH:mm:ss"));
            if (null != ITEM_Map.get(latestDto.getItemid())) {
                latestDto.setName(ITEM_Map.get(latestDto.getItemid()));
            }
        });

        return latestDtos.parallelStream().collect(Collectors.groupingBy(LatestDto::getName));
    }

    /**
     * 获取服务器 hostid
     *
     * @return
     */
    private String getZbxServerId() {
        String                    response = zbxHost.hostGet("Zabbix server");
        List<Map<String, String>> ids      = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            hostId = ids.get(0).get("hostid");
            return hostId;
        }
        return "";
    }

    /**
     * 获取服务器 取数的ITEM
     *
     * @return
     */
    private Map<String, String> getItemMap() {
        String            itemList  = zbxItem.getItemList(KEY, hostId);
        List<ZbxItemInfo> itemInfos = JSONObject.parseArray(itemList, ZbxItemInfo.class);
        for (ZbxItemInfo itemInfo : itemInfos) {
            ITEM_Map.put(itemInfo.getItemid(), formatName(itemInfo.getName()));
        }
        return ITEM_Map;
    }

    /**
     * 格式化显示的名称
     */
    private static String formatName(String name) {
        if (name.length() < 53) {
            return "avg";
        }
        return name.substring(35, name.length() - 18);
    }


    /**
     * 获取 数据图形展示
     *
     * @param response http响应
     * @param from     开始时间
     * @param to       结束时间
     * @param attrIds  设备属性ID
     * @param width    图表宽度
     * @param height   图表高度
     */
    public void getCharts(HttpServletResponse response,
                          String from, String to,
                          List<Long> attrIds, String width, String height) {
        if (ToolUtil.isEmpty(COOKIE) ||
                LocalDateTimeUtils.betweenTwoTime(COOKIE_TIME, LocalDateTime.now(), ChronoUnit.DAYS) >= 30) {
            getCookie();
        }

        List<String> itemids = getItemIds(attrIds);

        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod("http://" + zbxServerIp + ":" + zbxServerPort
                + "/zabbix/chart.php?type=0&profileIdx=web.item.graph.filter");

        NameValuePair[] nameValuePairs = new NameValuePair[itemids.size() + 4];
        nameValuePairs[0] = new NameValuePair("from", from);
        nameValuePairs[1] = new NameValuePair("to", to);
        nameValuePairs[2] = new NameValuePair("width", width);
        nameValuePairs[3] = new NameValuePair("height", height);

        for (int index = 0; index < itemids.size(); index++) {
            nameValuePairs[4 + index] = new NameValuePair("itemids[" + index + "]", itemids.get(index));
        }

        postMethod.setRequestBody(nameValuePairs);
        postMethod.setRequestHeader("Content_Type", "application/json-rpc");
        postMethod.setRequestHeader("Cookie", COOKIE);

        OutputStream out = null;
        try {
            client.executeMethod(postMethod);
            InputStream responseBody = postMethod.getResponseBodyAsStream();
            response.setContentType("image/jpeg");

            out = response.getOutputStream();
            out.write(toByteArray(responseBody));

            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int    n      = 0;

        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }

        return output.toByteArray();
    }


    private List<String> getItemIds(List<Long> attrIds) {
        return new QProductAttribute().select(QProductAttribute.alias().zbxId).attrId.in(attrIds).findSingleAttributeList();
    }

    /**
     * 用户访客 获取cookie
     */
    private void getCookie() {
        HttpClient client     = new HttpClient();
        PostMethod postMethod = new PostMethod("http://" + zbxServerIp + ":" + zbxServerPort + "/zabbix/index.php");

        //TODO 使用了一个只读权限的访客用户
        NameValuePair namePair      = new NameValuePair("name", "cookie");
        NameValuePair pwdPair       = new NameValuePair("password", "cookie");
        NameValuePair autologinPair = new NameValuePair("autologin", "1");
        NameValuePair enterPair     = new NameValuePair("enter", "Sign in");

        postMethod.setRequestBody(new NameValuePair[]{namePair, pwdPair, autologinPair, enterPair});
        postMethod.setRequestHeader("Content_Type", "application/json");

        try {
            client.executeMethod(postMethod);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        COOKIE = postMethod.getResponseHeader("Set-Cookie").getValue();
        COOKIE_TIME = LocalDateTime.now();
    }

    /**
     * 统计设备数量
     *
     * @return
     */
    public Map<String, Integer> getDeviceNum() {
        Map<String, Integer> deviceNumMap = new HashMap<>(4);

        deviceNumMap.put("online", 0);

        List<Device> list = new QDevice().findList();
        deviceNumMap.put("total", list.size());
        deviceNumMap.put("disable", (int) list.parallelStream().filter(o -> "DISABLE".equals(o.getStatus())).count());
        deviceNumMap.put("product", new QProduct().findCount());


        return deviceNumMap;
    }

}
