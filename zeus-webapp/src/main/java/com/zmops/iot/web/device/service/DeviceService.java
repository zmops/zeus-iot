package com.zmops.iot.web.device.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.Tag;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.device.query.QDevicesGroups;
import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.device.dto.param.DeviceParam;
import com.zmops.iot.web.device.dto.param.DeviceParams;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.zeus.driver.entity.Interface;
import com.zmops.zeus.driver.service.ZbxHost;
import com.zmops.zeus.driver.service.ZbxInterface;
import com.zmops.zeus.driver.service.ZbxValueMap;
import io.ebean.DB;
import io.ebean.DtoQuery;
import io.ebean.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class DeviceService implements CommandLineRunner {

    @Autowired
    private ZbxHost zbxHost;

    @Autowired
    private ZbxValueMap zbxValueMap;

    @Autowired
    DeviceGroupService deviceGroupService;

    @Autowired
    ZbxInterface zbxInterface;

    @Autowired
    DeviceEventPublisher deviceEventPublisher;

    /**
     * 设备列表
     *
     * @param deviceParams
     * @return
     */
    public List<Device> deviceList(DeviceParams deviceParams) {
        List<String> deviceIds = getDeviceIds();
        if (ToolUtil.isEmpty(deviceIds)) {
            return Collections.emptyList();
        }
        QDevice qDevice = new QDevice();

        qDevice.deviceId.in(deviceIds);

        if (ToolUtil.isNotEmpty(deviceParams.getName())) {
            qDevice.name.contains(deviceParams.getName());
        }
        if (ToolUtil.isNotEmpty(deviceParams.getDeviceId())) {
            qDevice.deviceId.eq(deviceParams.getDeviceId());
        }
        if (ToolUtil.isNotEmpty(deviceParams.getProductIds())) {
            qDevice.productId.in(deviceParams.getProductIds());
        }
        if (ToolUtil.isEmpty(deviceParams.getProductIds()) && ToolUtil.isNotEmpty(deviceParams.getProdTypes())) {
            List<Long> productIdList = new QProduct().select(QProduct.Alias.productId).groupId.in(
                    deviceParams.getProdTypes()).findSingleAttributeList();
            qDevice.productId.in(productIdList);
        }
        if (ToolUtil.isNotEmpty(deviceParams.getDeviceGroupIds())) {
            List<String> deviceList = new QDevicesGroups().select(QDevicesGroups.Alias.deviceId).deviceGroupId.in(
                    deviceParams.getDeviceGroupIds()).findSingleAttributeList();
            qDevice.deviceId.in(deviceList);
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
        return new QDevicesGroups().select(QDevicesGroups.alias().deviceId).deviceGroupId.in(devGroupIds)
                .findSingleAttributeList();
    }

    /**
     * 设备分页列表
     *
     * @param deviceParam
     * @return
     */
    public Pager<DeviceDto> devicePageList(DeviceParam deviceParam) {
        //根据当前用户所属用户组 取出绑定的主机组
        List<Long> devGroupIds = deviceGroupService.getDevGroupIds();
        if (ToolUtil.isEmpty(devGroupIds)) {
            return new Pager<>();
        }
        //组装查询SQL
        StringBuilder sql = generateBaseSql();
        sql.append(" where 1=1");

        //根据标签过滤
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
        //用于查询总记录数
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
                " d.tenant_id," +
                " d.proxy_id," +
                " P.NAME product_name, " +
                " P.product_code product_code, " +
                " ds.group_name, " +
                " ds.groupIds," +
                " m.method  " +
                "FROM " +
                " device d ")
                .append(" LEFT JOIN product P ON P.product_id = d.product_id ")
                .append(" INNER JOIN ( " +
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
                        "  ) ds ON ds.device_id = d.device_id ")
                .append(" left join device_service_method m on m.device_id = d.device_id ");
    }

    /**
     * 设备创建
     *
     * @param deviceDto
     * @return
     */
    public String create(DeviceDto deviceDto) {

        Device device = new Device();
        ToolUtil.copyProperties(deviceDto, device);

        device.setStatus(CommonStatus.ENABLE.getCode());
        DB.insert(device);

        deviceEventPublisher.DeviceSaveEventPublish(deviceDto);

        updateDeviceNameCache(deviceDto.getDeviceId(), deviceDto.getName());
        return deviceDto.getDeviceId();
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
        ToolUtil.copyProperties(deviceDto, device);
        deviceDto.setOldProductId(device.getProductId());
        deviceDto.setZbxId(device.getZbxId());

        DB.update(device);

        deviceEventPublisher.DeviceSaveEventPublish(deviceDto);

        updateDeviceNameCache(deviceDto.getDeviceId(), deviceDto.getName());
        return deviceDto.getDeviceId();
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

        deviceEventPublisher.DeviceDeleteEventPublish(device.getDeviceId(), zbxId);

        //更新设备名称缓存
        removeDeviceNameCache(deviceDto.getDeviceId());

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

        //先删除 之前的标签
        new QTag().sid.eq(productTag.getProductId()).delete();

        List<Tag> tags = new ArrayList<>();

        for (ProductTag.Tag tag : productTag.getProductTag()) {
            tags.add(Tag.builder().sid(productTag.getProductId()).tag(tag.getTag()).value(tag.getValue()).build());
        }

        DB.saveAll(tags);

        if (ToolUtil.isEmpty(zbxId)) {
            return;
        }

        //同步到Zbx
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
        DeviceDto deviceDto = DB.findDto(DeviceDto.class, sql.toString()).setParameter("deviceId", deviceId)
                .setParameter("deviceGroupIds", devGroupIds).findOne();

        if (deviceDto == null) {
            throw new ServiceException(BizExceptionEnum.DEVICE_NOT_EXISTS);
        }

        if (ToolUtil.isEmpty(deviceDto.getZbxId())) {
            return deviceDto;
        }

        String interfaceInfo = zbxInterface.hostinterfaceGet(deviceDto.getZbxId());
        List<Interface> interfaces = JSONObject.parseArray(interfaceInfo, Interface.class);
        if (ToolUtil.isNotEmpty(interfaces)) {
            deviceDto.setDeviceInterface(interfaces.get(0));
        }

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
    public void status(String status, String deviceId, String zbxId) {
        if (ToolUtil.isNotEmpty(zbxId)) {
            zbxHost.hostStatusUpdate(zbxId, "ENABLE".equals(status) ? "0" : "1");
        }
        DB.update(Device.class).where().eq("device_id", deviceId).asUpdate().set("status", status).setNull("online")
                .update();
    }

    /**
     * 更新设备名称缓存
     */
    private void updateDeviceCache() {
        List<Device> deviceList = new QDevice().select(QDevice.Alias.deviceId, QDevice.Alias.name).findList();
        Map<String, String> map = deviceList.parallelStream()
                .collect(Collectors.toMap(Device::getDeviceId, Device::getName, (a, b) -> a));
        DefinitionsUtil.updateDeviceCache(map);
    }

    /**
     * 更新设备名称缓存
     */
    private void updateDeviceNameCache(String deviceId, String name) {
        Map<String, String> all = DefinitionsUtil.getDeviceCache().getAll();
        all.put(deviceId, name);
        DefinitionsUtil.updateDeviceCache(all);
    }

    /**
     * 更新设备名称缓存
     */
    private void removeDeviceNameCache(String deviceId) {
        Map<String, String> all = DefinitionsUtil.getDeviceCache().getAll();
        all.remove(deviceId);
        DefinitionsUtil.updateDeviceCache(all);
    }

    @Override
    public void run(String... args) throws Exception {
        updateDeviceCache();
    }
}
