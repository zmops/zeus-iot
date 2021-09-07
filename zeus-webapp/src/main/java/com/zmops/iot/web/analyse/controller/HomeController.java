package com.zmops.iot.web.analyse.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.analyse.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author yefei
 * <p>
 * 全局概览
 **/
@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    HomeService homeService;

    /**
     * 设备数量统计
     */
    @RequestMapping("/deviceNum")
    public ResponseData getDeviceNum(){
        return ResponseData.success(homeService.getDeviceNum());
    }

    /**
     * 服务器取数速率
     *
     * @return
     */
    @RequestMapping("/collectonRate")
    public ResponseData collectonRate() {
        return ResponseData.success(homeService.collectonRate());
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
    @RequestMapping("/getCharts")
    public void getCookie(HttpServletResponse response,
                          @RequestParam("from") String from,
                          @RequestParam("to") String to,
                          @RequestParam("attrIds") List<Long> attrIds,
                          @RequestParam("width") String width,
                          @RequestParam("height") String height) {
        homeService.getCharts(response, from, to, attrIds, width, height);
    }
}
