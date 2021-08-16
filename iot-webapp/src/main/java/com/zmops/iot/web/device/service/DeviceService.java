package com.zmops.iot.web.device.service;

import com.zmops.iot.async.executor.Async;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.device.dto.param.DeviceParam;
import com.zmops.iot.web.device.service.work.*;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import io.ebean.DB;
import io.ebean.DtoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yefei
 **/
@Service
public class DeviceService {

    @Autowired
    SaveDeviceWorker saveDeviceWorker;

    @Autowired
    SaveAttributeWorker saveAttributeWorker;

    @Autowired
    SaveDeviceGrpWorker saveDeviceGrpWorker;

    @Autowired
    SaveTagWorker saveTagWorker;

    @Autowired
    SaveZbxHostWorker saveZbxHostWorker;

    @Autowired
    UpdateAttrZbxIdWorker updateAttrZbxIdWorker;

    @Autowired
    UpdateDeviceZbxIdWorker updateDeviceZbxIdWorker;

    /**
     * 设备分页列表
     *
     * @param deviceParam
     * @return
     */
    public Pager<DeviceDto> devicePageList(DeviceParam deviceParam) {
        QDevice qDevice = new QDevice();
        StringBuilder sql = new StringBuilder("SELECT " +
                " d.device_id, d.name, d.product_id, d.status, d.remark, d.create_time, d.create_user, d.update_time, d.update_user,d.type " +
                "FROM device d ");

        if (null != deviceParam.getDeviceGroupId()) {
            sql.append("left join devices_groups dg on dg.device_id=d.device_id where dg.device_group_id = :deviceGroupId");
        } else {
            sql.append(" where 1=1 ");
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
        sql.append(" order by d.create_time desc ");

        DtoQuery<DeviceDto> dto = DB.findDto(DeviceDto.class, sql.toString());

//        if (null != deviceParam.getDeviceGroupId()) {
//            qDevice.deviceGroupId.eq(deviceParam.getDeviceGroupId());
//        }
//        if (ToolUtil.isNotEmpty(deviceParam.getProdType())) {
//            qDevice.type.eq(deviceParam.getProdType());
//        }
//        if (ToolUtil.isNotEmpty(deviceParam.getProductId())) {
//            qDevice.productId.eq(deviceParam.getProductId());
//        }
//        if (ToolUtil.isNotEmpty(deviceParam.getName())) {
//            dto.setParameter("%" + deviceParam.getName() + "%");
//            qDevice.name.contains(deviceParam.getName());
//        }

        List<DeviceDto> list = qDevice.asDto(DeviceDto.class).setFirstRow((deviceParam.getPage() - 1) * deviceParam.getMaxRow())
                .setMaxRows(deviceParam.getMaxRow()).findList();

        return new Pager<>(list, qDevice.findCount());
    }

    /**
     * 设备创建
     *
     * @param deviceDto
     * @return
     */
    public Object create(DeviceDto deviceDto) {

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

        WorkerWrapper<String, Boolean> updateDeviceZbxIdWork = WorkerWrapper.<String, Boolean>builder().id("updateDeviceZbxIdWork")
                .worker(updateDeviceZbxIdWorker)
                .build();

        WorkerWrapper<DeviceDto, String> saveZbxHostWork = WorkerWrapper.<DeviceDto, String>builder().id("saveZbxHostWork")
                .worker(saveZbxHostWorker).param(deviceDto)
                .nextOf(updateAttrZbxIdWork, updateDeviceZbxIdWork)
                .build();

        WorkerWrapper<DeviceDto, Device> deviceWork = WorkerWrapper.<DeviceDto, Device>builder().id("saveDvice")
                .worker(saveDeviceWorker).param(deviceDto)
                .nextOf(saveTagWork, saveAttributeWork, saveDeviceGrpWork, saveZbxHostWork)
                .build();

        try {

            Async.work(1000, deviceWork).awaitFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceWork.getWorkResult().getResult();
    }

    /**
     * 设备修改
     *
     * @param deviceDto
     * @return
     */
    public Device update(DeviceDto deviceDto) {
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

//        WorkerWrapper<String, Boolean> updateDeviceZbxIdWork = WorkerWrapper.<String, Boolean>builder().id("updateDeviceZbxIdWork")
//                .worker(updateDeviceZbxIdWorker)
//                .build();

        WorkerWrapper<DeviceDto, String> saveZbxHostWork = WorkerWrapper.<DeviceDto, String>builder().id("saveZbxHostWork")
                .worker(saveZbxHostWorker).param(deviceDto)
                .nextOf(updateAttrZbxIdWork)
                .build();

        WorkerWrapper<DeviceDto, Device> deviceWork = WorkerWrapper.<DeviceDto, Device>builder().id("saveDvice")
                .worker(saveDeviceWorker).param(deviceDto)
                .nextOf(saveTagWork, saveAttributeWork, saveDeviceGrpWork, saveZbxHostWork)
                .build();

        try {

            Async.work(1000, deviceWork).awaitFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceWork.getWorkResult().getResult();
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

}
