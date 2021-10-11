package com.zmops.iot.web.device.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.async.executor.Async;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.Tag;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.device.query.QDevicesGroups;
import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.query.*;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.device.dto.param.DeviceParam;
import com.zmops.iot.web.device.service.work.*;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.zeus.driver.service.ZbxHost;
import com.zmops.zeus.driver.service.ZbxValueMap;
import io.ebean.DB;
import io.ebean.DtoQuery;
import io.ebean.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yefei
 **/
@Service
public class DeviceService {

    @Autowired
    private SaveDeviceWorker saveDeviceWorker;

    @Autowired
    private SaveAttributeWorker saveAttributeWorker;

    @Autowired
    private SaveDeviceGrpWorker saveDeviceGrpWorker;

    @Autowired
    private SaveTagWorker saveTagWorker;

    @Autowired
    private SaveZbxHostWorker saveZbxHostWorker;

    @Autowired
    private UpdateAttrZbxIdWorker updateAttrZbxIdWorker;

    @Autowired
    private UpdateDeviceZbxIdWorker updateDeviceZbxIdWorker;

    @Autowired
    private SaveOtherWorker saveOtherWorker;

    @Autowired
    private ZbxHost zbxHost;

    @Autowired
    private ZbxValueMap zbxValueMap;

    @Autowired
    DeviceGroupService deviceGroupService;

    /**
     * 设备列表
     *
     * @param deviceParam
     * @return
     */
    public List<Device> deviceList(DeviceParam deviceParam) {
        List<String> deviceIds = getDeviceIds();
        if (ToolUtil.isEmpty(deviceIds)) {
            return Collections.emptyList();
        }
        QDevice qDevice = new QDevice();

        qDevice.deviceId.in(deviceIds);

        if (ToolUtil.isNotEmpty(deviceParam.getName())) {
            qDevice.name.contains(deviceParam.getName());
        }
        return qDevice.findList();
    }

    /**
     * 获取当前用户 绑定的设备ID
     *
     * @return
     */
    public List<String> getDeviceIds() {
        List<Long> devGroupIds = deviceGroupService.getDevGroupIds();
        return new QDevicesGroups().select(QDevicesGroups.alias().deviceId).deviceGroupId.in(devGroupIds).findSingleAttributeList();
    }

    /**
     * 设备分页列表
     *
     * @param deviceParam
     * @return
     */
    public Pager<DeviceDto> devicePageList(DeviceParam deviceParam) {
        List<Long> devGroupIds = deviceGroupService.getDevGroupIds();
        if (ToolUtil.isEmpty(devGroupIds)) {
            return new Pager<>();
        }
        StringBuilder sql = generateBaseSql();
        sql.append(" where 1=1");
        List<Long> sids = new ArrayList<>();
        if (ToolUtil.isNotEmpty(deviceParam.getTag())) {
            QTag qTag = new QTag().select(QTag.Alias.sid).templateId.isNull();
            qTag.tag.contains(deviceParam.getTag());
            if (ToolUtil.isNotEmpty(deviceParam.getTagVal())) {
                qTag.value.contains(deviceParam.getTagVal());
            }
            sids = qTag.findSingleAttributeList();
            if (ToolUtil.isNotEmpty(sids)) {
                sql.append(" and d.device_id in ( :deviceIds )");
            }
        }
        if (ToolUtil.isNotEmpty(deviceParam.getProdType())) {
            sql.append(" and d.type = :prodType ");
        }
        if (ToolUtil.isNotEmpty(deviceParam.getProductId())) {
            sql.append(" and d.product_id = :prodId ");
        }
        if (ToolUtil.isNotEmpty(deviceParam.getName())) {
            sql.append(" and d.name like :name ");
        }
        Query<Device> count = DB.findNative(Device.class, sql.toString());

        sql.append(" order by d.create_time desc ");
        DtoQuery<DeviceDto> dto = DB.findDto(DeviceDto.class, sql.toString());

        if (ToolUtil.isNotEmpty(sids)) {
            dto.setParameter("deviceIds", sids);
            count.setParameter("deviceIds", sids);
        }
        if (null != deviceParam.getDeviceGroupId()) {
            dto.setParameter("deviceGroupId", deviceParam.getDeviceGroupId() + "");
            count.setParameter("deviceGroupId", deviceParam.getDeviceGroupId() + "");
        }
        if (ToolUtil.isNotEmpty(deviceParam.getProdType())) {
            dto.setParameter("prodType", deviceParam.getProdType());
            count.setParameter("prodType", deviceParam.getProdType());
        }
        if (ToolUtil.isNotEmpty(deviceParam.getProductId())) {
            dto.setParameter("prodId", deviceParam.getProductId());
            count.setParameter("prodId", deviceParam.getProductId());
        }
        if (ToolUtil.isNotEmpty(deviceParam.getName())) {
            dto.setParameter("name", "%" + deviceParam.getName() + "%");
            count.setParameter("name", "%" + deviceParam.getName() + "%");
        }
        if (null != deviceParam.getDeviceGroupId()) {
            if (devGroupIds.contains(deviceParam.getDeviceGroupId())) {
                dto.setParameter("deviceGroupIds", deviceParam.getDeviceGroupId());
                count.setParameter("deviceGroupIds", deviceParam.getDeviceGroupId());
            } else {
                return new Pager<>();
            }
        } else {
            dto.setParameter("deviceGroupIds", devGroupIds);
            count.setParameter("deviceGroupIds", devGroupIds);
        }


        List<DeviceDto> list = dto.setFirstRow((deviceParam.getPage() - 1) * deviceParam.getMaxRow())
                .setMaxRows(deviceParam.getMaxRow()).findList();

        return new Pager<>(list, count.findCount());
    }

    private StringBuilder generateBaseSql() {

        return new StringBuilder("SELECT " +
                " d.device_id," +
                " d.NAME, " +
                " d.product_id, " +
                " d.status, " +
                " d.remark, " +
                " d.create_time, " +
                " d.create_user, " +
                " d.update_time, " +
                " d.update_user, " +
                " d.TYPE, " +
                " d.addr, " +
                " d.position, " +
                " d.online," +
                " d.zbx_id," +
                " d.latest_online," +
                " P.NAME product_name, " +
                " ds.group_name, " +
                " ds.groupIds  " +
                "FROM " +
                " device d ")
                .append(" LEFT JOIN product P ON P.product_id = d.product_id ")
                .append(" LEFT JOIN ( " +
                        " SELECT  " +
                        "  d.device_id,  " +
                        "  array_to_string( ARRAY_AGG ( dg.NAME ), ',' ) group_name,  " +
                        "  array_to_string( ARRAY_AGG ( dg.device_group_id ), ',' ) groupIds   " +
                        " FROM  " +
                        "   device d  " +
                        "   LEFT JOIN devices_groups dgs ON dgs.device_id = d.device_id  " +
                        "   LEFT JOIN device_group dg ON dg.device_group_id = dgs.device_group_id   " +
                        " where dg.device_group_id in (:deviceGroupIds) " +
                        "  GROUP BY  d.device_id   " +
                        "  ) ds ON ds.device_id = d.device_id ");
    }

    /**
     * 设备创建
     *
     * @param deviceDto
     * @return
     */
    public String create(DeviceDto deviceDto) {

        WorkerWrapper<DeviceDto, Boolean> saveTagWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("saveTagWork")
                .worker(saveTagWorker).param(deviceDto).build();

        WorkerWrapper<DeviceDto, Boolean> saveAttributeWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("saveAttributeWork")
                .worker(saveAttributeWorker).param(deviceDto).build();

        WorkerWrapper<DeviceDto, Boolean> saveDeviceGrpWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("saveDeviceGrpWork")
                .worker(saveDeviceGrpWorker).param(deviceDto)
                .build();

        WorkerWrapper<DeviceDto, Boolean> updateAttrZbxIdWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("updateAttrZbxIdWork")
                .worker(updateAttrZbxIdWorker).param(deviceDto)
                .build();

        WorkerWrapper<String, Boolean> updateDeviceZbxIdWork = WorkerWrapper.<String, Boolean>builder().id("updateDeviceZbxIdWork")
                .worker(updateDeviceZbxIdWorker)
                .build();

        WorkerWrapper<DeviceDto, Boolean> saveOtherWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("saveOtherWork")
                .worker(saveOtherWorker).param(deviceDto)
                .build();

        WorkerWrapper<DeviceDto, String> saveZbxHostWork = WorkerWrapper.<DeviceDto, String>builder().id("saveZbxHostWork")
                .worker(saveZbxHostWorker).param(deviceDto)
                .nextOf(updateAttrZbxIdWork, updateDeviceZbxIdWork, saveOtherWork)
                .build();

        WorkerWrapper<DeviceDto, Device> deviceWork = WorkerWrapper.<DeviceDto, Device>builder().id("saveDvice")
                .worker(saveDeviceWorker).param(deviceDto)
                .nextOf(saveTagWork, saveAttributeWork, saveDeviceGrpWork, saveZbxHostWork)
                .build();

        try {

            Async.work(10000, deviceWork).awaitFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceWork.getWorkResult().getResult().getDeviceId();
    }

    /**
     * 设备修改
     *
     * @param deviceDto
     * @return
     */
    public String update(DeviceDto deviceDto) {
        Device device = new QDevice().deviceId.eq(deviceDto.getDeviceId()).findOne();
        if (null == device) {
            throw new ServiceException(BizExceptionEnum.DEVICE_NOT_EXISTS);
        }
        deviceDto.setOldProductId(device.getProductId());
        deviceDto.setZbxId(device.getZbxId());

        WorkerWrapper<DeviceDto, Boolean> saveTagWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("saveTagWork")
                .worker(saveTagWorker).param(deviceDto).build();

        WorkerWrapper<DeviceDto, Boolean> saveAttributeWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("saveAttributeWork")
                .worker(saveAttributeWorker).param(deviceDto).build();

        WorkerWrapper<DeviceDto, Boolean> saveDeviceGrpWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("saveDeviceGrpWork")
                .worker(saveDeviceGrpWorker).param(deviceDto)
                .build();

        WorkerWrapper<DeviceDto, Boolean> updateAttrZbxIdWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("updateAttrZbxIdWork")
                .worker(updateAttrZbxIdWorker)
                .build();

        WorkerWrapper<DeviceDto, Boolean> saveOtherWork = WorkerWrapper.<DeviceDto, Boolean>builder().id("saveOtherWork")
                .worker(saveOtherWorker).param(deviceDto)
                .build();

        WorkerWrapper<DeviceDto, String> saveZbxHostWork = WorkerWrapper.<DeviceDto, String>builder().id("saveZbxHostWork")
                .worker(saveZbxHostWorker).param(deviceDto)
                .nextOf(updateAttrZbxIdWork, saveOtherWork)
                .build();

        WorkerWrapper<DeviceDto, Device> deviceWork = WorkerWrapper.<DeviceDto, Device>builder().id("saveDvice")
                .worker(saveDeviceWorker).param(deviceDto)
                .nextOf(saveTagWork, saveAttributeWork, saveDeviceGrpWork, saveZbxHostWork)
                .build();

        try {

            Async.work(10000, deviceWork).awaitFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceWork.getWorkResult().getResult().getDeviceId();
    }

    /**
     * 检查产品是否存在 并把赋值type
     *
     * @param deviceDto
     */
    public void checkProductExist(DeviceDto deviceDto) {
        Product product = new QProduct().productId.eq(deviceDto.getProductId()).findOne();
        if (null == product) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        deviceDto.setType(product.getType());
    }

    public String delete(DeviceDto deviceDto) {
        Device device = new QDevice().deviceId.eq(deviceDto.getDeviceId()).findOne();
        if (null == device) {
            return deviceDto.getDeviceId();
        }
        String zbxId = device.getZbxId();
        WorkerWrapper<String, Boolean> delTagWork = WorkerWrapper.<String, Boolean>builder().id("delTagWork")
                .worker((deviceId, allWrappers) -> {
                    new QTag().sid.eq(deviceId).delete();
                    return true;
                }).param(deviceDto.getDeviceId()).build();

        WorkerWrapper<String, Boolean> delAttrWork = WorkerWrapper.<String, Boolean>builder().id("delAttrWork")
                .worker((deviceId, allWrappers) -> {
                    new QProductAttribute().productId.eq(deviceId).delete();
                    return true;
                }).param(deviceDto.getDeviceId()).build();

        WorkerWrapper<String, Boolean> delGropusWork = WorkerWrapper.<String, Boolean>builder().id("delGropusWork")
                .worker((deviceId, allWrappers) -> {
                    new QDevicesGroups().deviceId.eq(deviceId).delete();
                    return true;
                }).param(deviceDto.getDeviceId()).build();

        WorkerWrapper<String, Boolean> delZbxWork = WorkerWrapper.<String, Boolean>builder().id("delZbxWork")
                .worker((zbxid, allWrappers) -> {
                    if (ToolUtil.isNotEmpty(zbxid)) {
                        JSONArray jsonArray = JSONObject.parseArray(zbxHost.hostDetail(zbxId));
                        if (jsonArray.size() > 0) {
                            zbxHost.hostDelete(Collections.singletonList(zbxid));
                        }
                    }
                    return true;
                }).param(zbxId).build();

        WorkerWrapper<String, Boolean> delOtherWork = WorkerWrapper.<String, Boolean>builder().id("delOtherWork")
                .worker((deviceId, allWrappers) -> {
                    new QProductStatusFunctionRelation().relationId.eq(deviceId).delete();
                    new QProductServiceRelation().relationId.eq(deviceId).delete();
                    new QProductEventRelation().relationId.eq(deviceId).delete();
                    new QProductEventService().deviceId.eq(deviceId).delete();
                    return true;
                }).param(deviceDto.getDeviceId()).build();

        WorkerWrapper<String, Boolean> delDeviceWork = WorkerWrapper.<String, Boolean>builder().id("delDeviceWork")
                .worker((deviceId, allWrappers) -> {
                    new QDevice().deviceId.eq(deviceId).delete();
                    return true;
                }).param(deviceDto.getDeviceId())
                .nextOf(delTagWork, delAttrWork, delGropusWork, delZbxWork, delOtherWork).build();

        try {

            Async.work(5000, delDeviceWork).awaitFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return deviceDto.getDeviceId();
    }

    /**
     * 设备标签修改
     *
     * @param productTag
     * @param zbxId
     */
    public void deviceTagCreate(ProductTag productTag, String zbxId) {
        if (ToolUtil.isEmpty(productTag.getProductTag())) {
            return;
        }

        new QTag().sid.eq(productTag.getProductId()).delete();
        List<Tag> tags = new ArrayList<>();

        for (ProductTag.Tag tag : productTag.getProductTag()) {
            tags.add(Tag.builder().sid(productTag.getProductId()).tag(tag.getTag()).value(tag.getValue()).build());
        }

        DB.saveAll(tags);

        if (ToolUtil.isEmpty(zbxId)) {
            return;
        }

        List<Tag> list = new QTag().sid.eq(productTag.getProductId()).findList();

        Map<String, String> tagMap = new HashMap<>(list.size());

        for (Tag tag : list) {
            tagMap.put(tag.getTag(), tag.getValue());
        }

        //保存
        zbxHost.hostTagUpdate(zbxId, tagMap);
    }

    /**
     * 创建产品 值映射
     *
     * @param hostid       产品模板ID
     * @param valueMapName 名称
     * @param valueMaps    KV
     * @return
     */
    public String valueMapCreate(String hostid, String valueMapName, Map<String, String> valueMaps) {
        return zbxValueMap.valueMapCreate(hostid, valueMapName, valueMaps);
    }

    /**
     * 修改 产品值映射
     *
     * @param hostid       产品模板ID
     * @param valueMapName 名称
     * @param valueMaps    KV
     * @param valueMapId   映射ID
     * @return
     */
    public String valueMapUpdate(String hostid, String valueMapName, Map<String, String> valueMaps, String valueMapId) {
        return zbxValueMap.valueMapUpdate(hostid, valueMapName, valueMaps, valueMapId);
    }

    /**
     * 删除值映射
     *
     * @param valueMapId 值映射ID
     * @return String
     */
    public String valueMapDelete(String valueMapId) {
        return zbxValueMap.valueMapDelete(valueMapId);
    }

    /**
     * 设备详情
     *
     * @param deviceId
     * @return
     */
    public DeviceDto deviceDetail(String deviceId) {
        List<Long> devGroupIds = deviceGroupService.getDevGroupIds();
        if (ToolUtil.isEmpty(devGroupIds)) {
            return new DeviceDto();
        }
        StringBuilder sql = generateBaseSql();
        sql.append(" where d.device_id=:deviceId");
        DeviceDto deviceDto = DB.findDto(DeviceDto.class, sql.toString()).setParameter("deviceId", deviceId).setParameter("deviceGroupIds", devGroupIds).findOne();
        return deviceDto;
    }

    /**
     * 设备标签列表
     *
     * @param deviceId
     * @return
     */
    public List<Tag> deviceTagList(String deviceId) {
        QTag tag = QTag.alias();
        return new QTag().select(tag.id, tag.sid, tag.tag, tag.value).sid.eq(deviceId).findList();
    }

    /**
     * 设备值映射列表
     *
     * @param deviceId
     * @return
     */
    public JSONArray valueMapList(String deviceId) {
        JSONArray zbxTemplateInfo = getZbxHostInfo(deviceId);
        if (zbxTemplateInfo.size() == 0) {
            return new JSONArray();
        }
        //查询ZBX valueMap
        return JSONObject.parseArray(zbxTemplateInfo.getJSONObject(0).getString("valuemaps"));
    }

    private JSONArray getZbxHostInfo(String deviceId) {
        String zbxId = new QDevice().select(QDevice.alias().zbxId).deviceId.eq(deviceId).findSingleAttribute();
        if (null == zbxId) {
            return new JSONArray();
        }
        return JSONObject.parseArray(zbxHost.hostDetail(zbxId));
    }

    /**
     * 修改设备状态
     *
     * @param status
     * @param deviceId
     * @return
     */
    public void status(String status, String deviceId) {
        DB.update(Device.class).where().eq("device_id", deviceId).asUpdate().set("status", status).setNull("online").update();
    }
}
