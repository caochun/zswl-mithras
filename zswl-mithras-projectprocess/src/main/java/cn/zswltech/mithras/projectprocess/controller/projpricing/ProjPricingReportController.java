package cn.zswltech.mithras.projectprocess.controller.projpricing;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projpricing.report.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import cn.zswltech.mithras.api.projpricing.ProjPricingReportApi;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingReportApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjPricingReportController implements ProjPricingReportApi {
    @Resource
    private ProjPricingReportApplicationService projPricingReportApplicationService;

    @Override
    public R< List<Pair<String,List<ProjPricingReportListRSP>>>> list(ProjPricingReportListREQ ProjPricingReportListREQ) {
        return projPricingReportApplicationService.list(ProjPricingReportListREQ);
    }

    @Override
    public R<Long> generate(ProjPricingReportGenerateREQ ProjPricingReportGenerateREQ) throws Exception {
        return projPricingReportApplicationService.generate(ProjPricingReportGenerateREQ);
    }

    @Override
    public R<FileListRSP> download(ProjPricingReportDownloadREQ ProjPricingReportDownloadREQ) throws IOException {
        return projPricingReportApplicationService.download(ProjPricingReportDownloadREQ);
    }

    @Override
    public R<Void> upload(MultipartFile file, ProjPricingReportUploadREQ ProjPricingReportUploadREQ) {
        return projPricingReportApplicationService.upload(file, ProjPricingReportUploadREQ);
    }

    @Override
    public R<Void> remove(ProjPricingReportRemoveREQ ProjPricingReportRemoveREQ) {
        return projPricingReportApplicationService.remove(ProjPricingReportRemoveREQ);
    }
}
