package com.zmops.zeus.iot.web.controller;

import com.zmops.zeus.iot.server.h2.module.LocalH2Module;
import com.zmops.zeus.iot.server.h2.service.InsertDAO;
import com.zmops.zeus.server.library.module.ModuleManager;
import com.zmops.zeus.server.library.web.core.Controller;
import com.zmops.zeus.server.library.web.core.Path;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yefei
 **/
@Path(value = "/protocol/service")
public class ProtocolServiceController extends Controller {

    public void saveProtocolService() {
        String id = getPara("protocolServiceId");
        String name = getPara("name");
        String remark = getPara("remark");
        String url = getPara("url");
        String ip = getPara("ip");
        String port = getPara("port");
        String msgLength = getPara("msgLength");
        String clientId = getPara("clientId");
        String protocol = getPara("protocol");
        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);
        String sql = "insert into protocol_service(id,name,url,ip,port,msg_length,client_id,protocol,remark) " +
                " values(" + id + ",'" + name + "','" + url + "','" + ip + "','" + port + "','" + msgLength + "','" + clientId + "','" + protocol + "','" + remark + "')";
        localH2InsertDAO.insert(sql);

        renderNull();
    }

    public void updateProtocolService() {
        String id = getPara("protocolServiceId");
        String name = getPara("name");
        String remark = getPara("remark");
        String url = getPara("url");
        String ip = getPara("ip");
        String port = getPara("port");
        String msgLength = getPara("msgLength");
        String clientId = getPara("clientId");
        String protocol = getPara("protocol");

        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);

        StringBuilder sql = new StringBuilder("update protocol_service set ip=?,port=?");
        if(StringUtils.isNotBlank(name)){
            sql.append(" ,name =?");
        }
        if(StringUtils.isNotBlank(remark)){
            sql.append(" ,remark =?");
        }
        if(StringUtils.isNotBlank(url)){
            sql.append(" ,url =?");
        }
        if(StringUtils.isNotBlank(msgLength)){
            sql.append(" ,msg_length =?");
        }
        if(StringUtils.isNotBlank(clientId)){
            sql.append(" ,client_id =?");
        }
        if(StringUtils.isNotBlank(protocol)){
            sql.append(" ,protocol =?");
        }

        sql.append(" where id=?");
        localH2InsertDAO.update(sql.toString(), ip, port, name, remark, url,  msgLength, clientId, protocol, id);

        renderNull();
    }

}
