package cn.zswltech.mithras.ftp.oldftp.service.application;

import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.ftp.FtpBatchIdsReq;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListRSP;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListReq;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.util.List;

public interface FtpMaterialsFileApplicationService {

    List<FtpMaterialListRSP> fileList(FtpMaterialListReq req);

    void fileUpload(MultipartFile file, String bizType, Long belongId);

    void fileRemove(FtpBatchIdsReq req);

    FileListRSP fileDownload(FtpBatchIdsReq req, ServletOutputStream outputStream);
}
