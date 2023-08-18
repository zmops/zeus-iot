package com.zmops.zeus.iot.server.receiver.service;

import com.zmops.zeus.facade.DynamicProcotol;
import com.zmops.zeus.server.library.module.ModuleManager;
import com.zmops.zeus.server.library.module.Service;
import com.zmops.zeus.server.runtime.api.client.ReferenceClient;
import com.zmops.zeus.server.runtime.api.client.param.ReferenceParam;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nantian created at 2021/12/21 15:20
 * <p>
 * 动态协议代理
 */
public class ReferenceClientService implements Service {

    private final ReferenceClient referenceClient;
    private final ModuleManager moduleManager;

    // 缓存调用 客户端
    private static final ConcurrentHashMap<String, DynamicProcotol> dynamicProtoMap = new ConcurrentHashMap<>();

    public ReferenceClientService(ReferenceClient client, ModuleManager moduleManager) {
        this.referenceClient = client;
        this.moduleManager = moduleManager;
    }


    public ReferenceClient getReferenceClient() {
        return referenceClient;
    }


    /**
     * 根据服务ID，动态获取代理客户端
     *
     * @param serviceId -
     * @return DynamicProcotol
     */
    public DynamicProcotol getDynamicProtocolClient(String serviceId) {

        DynamicProcotol client = dynamicProtoMap.get(serviceId);
        if (client != null) {
            return client;
        }

        ReferenceParam<DynamicProcotol> referenceParam = new ReferenceParam<>();
        referenceParam.setInterfaceType(DynamicProcotol.class);
        referenceParam.setUniqueId(serviceId);

        DynamicProcotol client2 = referenceClient.reference(referenceParam);
        dynamicProtoMap.put(serviceId, client2);

        return client2;
    }
}
