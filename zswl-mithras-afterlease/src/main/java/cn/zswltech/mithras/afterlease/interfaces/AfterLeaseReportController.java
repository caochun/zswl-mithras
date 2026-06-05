package cn.zswltech.mithras.afterlease.interfaces;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.afterlease.application.AfterLeaseReportApplicationService;
import cn.zswltech.mithras.api.afterlease.AfterLeaseReportApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportUploadREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportDownloadREQ;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportListRSP;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportRemoveREQ;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class AfterLeaseReportController implements AfterLeaseReportApi {

    @Resource
    private AfterLeaseReportApplicationService afterLeaseReportApplicationService;

    @Override
    public R<List<Pair<String, List<ProjReviewReportListRSP>>>> list(@Valid AfterLeaseReportListREQ req) {
        return afterLeaseReportApplicationService.list(req);
    }

    @Override
    public R<FileListRSP> download(@Valid ProjReviewReportDownloadREQ req) {
        return afterLeaseReportApplicationService.download(req);
    }

    @Override
    public R<Void> upload(MultipartFile file, AfterLeaseReportUploadREQ req) {
        return afterLeaseReportApplicationService.upload(file, req);
    }

    @Override
    public R<Void> remove(@Valid ProjReviewReportRemoveREQ projReviewReportRemoveREQ) {
        return afterLeaseReportApplicationService.remove(projReviewReportRemoveREQ);
    }
}
