package com.zmops.iot.web.analyse.service;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.service.ZbxItem;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author yefei
 * <p>
 * 全局概览服务
 **/
@Service
public class ZbxChartsService {

    @Value("${forest.variables.zbxServerIp}")
    private String zbxServerIp;

    @Value("${forest.variables.zbxServerPort}")
    private String zbxServerPort;

    private static String COOKIE = "";

    private static LocalDateTime COOKIE_TIME;

    @Autowired
    ZbxItem zbxItem;

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
        OutputStream out = null;
        if (ToolUtil.isEmpty(COOKIE) ||
                LocalDateTimeUtils.betweenTwoTime(COOKIE_TIME, LocalDateTime.now(), ChronoUnit.DAYS) >= 30) {
            getCookie();
        }

        List<String> itemids = getItemIds(attrIds);
        if (!validItemInfo(itemids)) {
            try {
                ClassPathResource classPathResource = new ClassPathResource("/nodata.jpg");
                InputStream inputStream = classPathResource.getInputStream();

                response.setContentType("image/jpeg");

                out = response.getOutputStream();
                out.write(toByteArray(inputStream));

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
            return;
        }

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

    private boolean validItemInfo(List<String> itemids) {
        if (ToolUtil.isEmpty(itemids)) {
            return false;
        }

        List<ZbxItemInfo> itemInfos = JSONObject.parseArray(zbxItem.getItemInfo(itemids.toString(), null), ZbxItemInfo.class);
        if (ToolUtil.isEmpty(itemInfos)) {
            return false;
        }
        return true;
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int n = 0;

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
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod("http://" + zbxServerIp + ":" + zbxServerPort + "/zabbix/index.php");

        //TODO 使用了一个只读权限的访客用户
        NameValuePair namePair = new NameValuePair("name", "cookie");
        NameValuePair pwdPair = new NameValuePair("password", "cookie");
        NameValuePair autologinPair = new NameValuePair("autologin", "1");
        NameValuePair enterPair = new NameValuePair("enter", "Sign in");

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


}
