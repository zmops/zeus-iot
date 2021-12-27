package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.callback.OnProgress;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yefei
 **/
public interface ZeusServer {

    /**
     * 上传 协议包
     *
     * @param ip         服务器IP
     * @param file       文件
     * @param onProgress 进度处理
     * @return map
     */
    @Post(url = "http://${ip}:12800/protocol/component/upload")
    void upload(@Var("ip") String ip, @DataFile("file") MultipartFile file, OnProgress onProgress);
}
