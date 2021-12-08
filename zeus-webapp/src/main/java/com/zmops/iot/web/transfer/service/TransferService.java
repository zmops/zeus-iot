package com.zmops.iot.web.transfer.service;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ParseUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.transfer.dto.ConfigDto;
import com.zmops.iot.web.transfer.dto.StatusDto;
import com.zmops.iot.web.transfer.dto.TransferDto;
import com.zmops.iot.web.transfer.dto.param.TransferParam;
import com.zmops.zeus.driver.service.TransferKit;
import com.zmops.zeus.driver.service.ZbxConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author yefei
 * <p>
 * 数据转换 服务
 **/
@Service
public class TransferService {

    @Autowired
    TransferKit transferKit;

    @Autowired
    ZbxConfig zbxConfig;

    /**
     * 转换服务列表
     *
     * @return
     */
    public List<TransferDto> list() {
        String configs = transferKit.getConfigs();
        if (ToolUtil.isEmpty(configs)) {
            return Collections.emptyList();
        }
        JSONObject              jsonObject      = JSONObject.parseObject(configs);
        List<TransferDto>       transferDtoList = new ArrayList<>();
        HashMap<String, Object> hashMap         = JSONObject.parseObject(jsonObject.getString("data"), HashMap.class);
        for (Map.Entry<String, Object> s : hashMap.entrySet()) {
            ConfigDto configDto = JSONObject.parseObject(JSONObject.toJSONString(s.getValue()), ConfigDto.class);
            transferDtoList.add(BuildTransferDto(configDto));
        }

        String status = transferKit.getStatus();
        if (ToolUtil.isEmpty(status)) {
            return transferDtoList;
        }
        JSONObject              statusObject = JSONObject.parseObject(status);
        HashMap<String, Object> statusMap    = JSONObject.parseObject(statusObject.getString("data"), HashMap.class);
        transferDtoList.forEach(transferDto -> {
            if (null != statusMap.get(transferDto.getName())) {
                StatusDto statusDto = JSONObject.parseObject(JSONObject.toJSONString(statusMap.get(transferDto.getName())), StatusDto.class);
                transferDto.setLogpath(statusDto.getLogpath());

                transferDto.setReadDataCount(ParseUtil.getCommaFormat(statusDto.getReadDataCount()));
                transferDto.setReadLastError(statusDto.getReaderStats().getLast_error());
                transferDto.setLagSize(ParseUtil.formatLagSize(statusDto.getLag().getSize()));
                transferDto.setParseError(ToolUtil.isEmpty(statusDto.getParserStats().getErrors()) ? "0" : statusDto.getParserStats().getErrors());
                transferDto.setParseSuccess(ToolUtil.isEmpty(statusDto.getParserStats().getSuccess()) ? "0" : statusDto.getParserStats().getSuccess());
                transferDto.setParseTotal(transferDto.getParseSuccess() + transferDto.getParseError());
                transferDto.setParseLastError(statusDto.getParserStats().getLast_error());

                HashMap<String, Object> sendStatusMap = JSONObject.parseObject(statusDto.getSenderStats(), HashMap.class);
                for (Map.Entry map : sendStatusMap.entrySet()) {
                    StatusDto.SenderStatsObj senderStatsObj = JSONObject.parseObject(JSONObject.toJSONString(map.getValue()), StatusDto.SenderStatsObj.class);
                    transferDto.setSendSuccess(ToolUtil.isEmpty(senderStatsObj.getSuccess()) ? "0" : senderStatsObj.getSuccess());
                    transferDto.setSendError(ToolUtil.isEmpty(senderStatsObj.getErrors()) ? "0" : senderStatsObj.getErrors());
                    transferDto.setSendTotal(transferDto.getSendSuccess() + transferDto.getSendError());
                    transferDto.setSendspeed(ToolUtil.isEmpty(senderStatsObj.getSpeed()) ? "0" : senderStatsObj.getSpeed());
                    transferDto.setSendLastError(senderStatsObj.getLast_error());
                }

                transferDto.setReadspeed(statusDto.getReadspeed());
                transferDto.setReadspeedKb(statusDto.getReadspeedKb());
                transferDto.setRunningStatus(statusDto.getRunningStatus().equalsIgnoreCase("running") ? "ENABLE" : "DISABLE");
            }
        });

        return transferDtoList;
    }

    private TransferDto BuildTransferDto(ConfigDto configDto) {
        TransferDto transferDto = new TransferDto();
        transferDto.setName(configDto.getName());
        transferDto.setCreatetime(configDto.getCreatetime());
        return transferDto;
    }

    /**
     * 转换服务创建
     *
     * @return
     */
    public void create() {
        String config = zbxConfig.getConfig();
        if (ToolUtil.isEmpty(config)) {
            throw new ServiceException(BizExceptionEnum.ZBX_SERBER_NOT_CONFIG);
        }
        Map<String, String> map            = JSONObject.parseObject(config, Map.class);
        String              exportPath     = map.get("ExportDir");
        String              StartDBSyncers = map.get("StartDBSyncers");
        if (null == exportPath || "".equals(exportPath) || null == StartDBSyncers || "".equals(StartDBSyncers)) {
            throw new ServiceException(BizExceptionEnum.ZBX_SERBER_EXPORT_PATH_NOT_CONFIG);
        }
        int dbSyncers = Integer.parseInt(StartDBSyncers);
        for (int i = 1; i <= dbSyncers; i++) {
            String logPath = exportPath + "/history-history-syncer-" + i + ".ndjson";
            String name    = "runner:" + LocalDateTimeUtils.getMilliByTime(LocalDateTime.now());
            transferKit.createRunner(name, name, logPath, 20, 2097152);
        }
    }


    /**
     * 转换服务删除
     *
     * @param names
     * @return
     */
    public void delete(List<String> names) {
        names.forEach(name -> {
            transferKit.deleteRunner(name);
        });
    }

    /**
     * 数据转换启动
     */
    public void run(TransferParam transferParam) {
        if (ToolUtil.isEmpty(transferParam.getRun()) || !"true".equals(transferParam.getRun())) {
            stop(transferParam);
        }
        transferParam.getNames().forEach(name -> {
            transferKit.startRunner(name);
        });
    }

    /**
     * 数据转换停止
     */
    public void stop(TransferParam transferParam) {
        transferParam.getNames().forEach(name -> {
            transferKit.stopRunner(name);
        });
    }
}
