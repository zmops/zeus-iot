package com.zmops.zeus.iot.web.controller;

import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.api.ClientResponse;
import com.zmops.zeus.iot.server.h2.module.LocalH2Module;
import com.zmops.zeus.iot.server.h2.service.InsertDAO;
import com.zmops.zeus.server.library.module.ModuleManager;
import com.zmops.zeus.server.library.web.core.Controller;
import com.zmops.zeus.server.library.web.core.Path;
import com.zmops.zeus.server.library.web.upload.UploadFile;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yefei
 **/
@Path(value = "/protocol/component")
public class ProtocolComponentController extends Controller {

    public void saveProtocolComponent() {
        String id = getPara("protocolComponentId");
        String uniqueId = getPara("uniqueId");
        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);
        localH2InsertDAO.insert("insert into protocol_component(id,unique_id) values(" + id + "," + uniqueId + ")");

        renderNull();
    }

    public void upload() {
        UploadFile file = getFile();

        try {
            String filePath = file.getFile().getCanonicalPath();

            renderJson("filePath", filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void installArk() throws SQLException {
        String id = getPara("protocolComponentId");
        String fileName = getPara("fileName");
        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);

        try {
            ClientResponse response = ArkClient.installBiz(new File("D:\\666\\upload\\" + fileName));
//            ClientResponse response = ArkClient.installBiz(new File("//opt//zeus//zeus-iot-bin//upload//" + fileName));
            String bizName = response.getBizInfos().iterator().next().getBizName();
            String bizVersion = response.getBizInfos().iterator().next().getBizVersion();
            int r = localH2InsertDAO.update("update protocol_component set file_name=?, biz_name=?,biz_version = ? where id=?", fileName, bizName, bizVersion, id);

            renderJson(response);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void uninstallArk() throws SQLException {
        String id = getPara("protocolComponentId");
        InsertDAO localH2InsertDAO = ModuleManager.getInstance()
                .find(LocalH2Module.NAME).provider().getService(InsertDAO.class);
        ResultSet rs = localH2InsertDAO.queryRes("select * from protocol_component where id=?", id);
        String bizName = "";
        String bizVersion = "";
        while (rs.next()) {
            bizName = rs.getString(7);
            bizVersion = rs.getString(8);
        }
        try {
            ClientResponse response = ArkClient.uninstallBiz(bizName, bizVersion);
            renderJson(response);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
