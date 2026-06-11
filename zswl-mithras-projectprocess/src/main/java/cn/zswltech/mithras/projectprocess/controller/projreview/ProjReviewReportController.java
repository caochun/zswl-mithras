package cn.zswltech.mithras.projectprocess.controller.projreview;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projreview.report.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import cn.zswltech.mithras.api.projreview.ProjReviewReportApi;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewReportApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjReviewReportController implements ProjReviewReportApi {
    @Resource
    private ProjReviewReportApplicationService projReviewReportApplicationService;

    @Override
    public R< List<Pair<String,List<ProjReviewReportListRSP>>>> list(ProjReviewReportListREQ projReviewReportListREQ) {
        return projReviewReportApplicationService.list(projReviewReportListREQ);
    }

    @Override
    public R<Long> generate(ProjReviewReportGenerateREQ projReviewReportGenerateREQ) throws Exception {
        return projReviewReportApplicationService.generate(projReviewReportGenerateREQ);
    }

    @Override
    public R<FileListRSP> download(ProjReviewReportDownloadREQ projReviewReportDownloadREQ) throws IOException {
        return projReviewReportApplicationService.download(projReviewReportDownloadREQ);
    }

    @Override
    public R<Void> upload(MultipartFile file, ProjReviewReportUploadREQ projReviewReportUploadREQ) {
        return projReviewReportApplicationService.upload(file, projReviewReportUploadREQ);
    }

    @Override
    public R<Void> remove(ProjReviewReportRemoveREQ projReviewReportRemoveREQ) {
        return projReviewReportApplicationService.remove(projReviewReportRemoveREQ);
    }

    @Override
    public R<Long> generateEarningsRate(ProjReviewReportGenerateREQ projReviewReportGenerateREQ) {
        return projReviewReportApplicationService.generateEarningsRate(projReviewReportGenerateREQ);
    }
}
