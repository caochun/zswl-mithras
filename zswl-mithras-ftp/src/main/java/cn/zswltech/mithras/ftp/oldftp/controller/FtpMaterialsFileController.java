package cn.zswltech.mithras.ftp.oldftp.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.ftp.FtpMaterialsFileApi;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.ftp.FtpBatchIdsReq;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListRSP;
import cn.zswltech.mithras.dto.ftp.FtpMaterialListReq;
import cn.zswltech.mithras.ftp.oldftp.application.FtpMaterialsFileApplicationService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/10 10:00
 */
@RestController
public class FtpMaterialsFileController implements FtpMaterialsFileApi {
    @Resource
    private FtpMaterialsFileApplicationService materialsFileService;
    @Resource
    private HttpServletResponse response;

    @Override
    public R<List<FtpMaterialListRSP>> fileList(FtpMaterialListReq req) {
        return R.ok(materialsFileService.fileList(req));
    }

    @Override
    public R<Void> fileUpload(MultipartFile file, String bizType, Long belongId) {
        materialsFileService.fileUpload(file, bizType, belongId);
        return R.ok();
    }

    @Override
    public R<Void> fileRemove(FtpBatchIdsReq req) {
        materialsFileService.fileRemove(req);
        return R.ok();
    }

    @Override
    @Deprecated
    public R<FileListRSP> fileDownload(FtpBatchIdsReq req) {
        try {
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资料清单.zip", StandardCharsets.UTF_8.name()));
            return R.ok(materialsFileService.fileDownload(req, response.getOutputStream()));
        } catch (Exception e) {
            throw new MithrasException("下载失败!");
        }
    }
}
