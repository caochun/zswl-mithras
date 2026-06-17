package cn.zswltech.mithras.application.orchestration.document.materialsfile.batchdownload.handle;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.file.FileBatchDownLoadREQ;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportDownloadService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.batchdownload.AbstractFileBatchDownload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName DefaultBatchDownload
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/13 10:58 上午
 * @Version 1.0
 **/
//@Component
@Slf4j
public class AfterLeaseCheckReportHandle extends AbstractFileBatchDownload {

    @Resource
    private HttpServletResponse httpServletResponse;

    @Resource
    private AfterLeaseCheckReportDownloadService afterLeaseCheckReportDownloadService;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name();
    }

    @Override
    public void batchDownload(FileBatchDownLoadREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租后检查报告" + GlobalConstants.COMPRESSED_FILE_SUFFIX, StandardCharsets.UTF_8.name()));
            afterLeaseCheckReportDownloadService.downloadBatchClientReport(httpServletResponse.getOutputStream(), req.getFileId());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载租后检查报告生未知异常[checkPlanProjectIds: {}]", JSONUtil.toJsonStr(req.getFileId()),  e);
            throw new MithrasException("下载租后检查报告生未知异常");
        }
    }
}
