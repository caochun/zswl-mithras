package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportUploadREQ;
import cn.zswltech.mithras.document.model.MaterialsList;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.List;

/**
 * @ClassName AfterLeaseReportService
 * @Description
 * @Author jackerhe
 * @Date 2022/11/11 11:41 上午
 * @Version 1.0
 **/
public interface AfterLeaseReportService {

    //上传文件
    @Deprecated
    void upload(MultipartFile multipartFile, AfterLeaseReportUploadREQ req);

    //下载文件
    void download(OutputStream outputStream, Long materialsId);

    //list
    List<MaterialsList> list(AfterLeaseReportListREQ req);

    void remove(Long materialsId);

}
