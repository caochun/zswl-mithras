package cn.zswltech.mithras.projectprocess.controller.projreview;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanExportREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.projreview.ProjReviewCashFlowPlanApi;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewCashFlowPlanApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjReviewCashFlowPlanController implements ProjReviewCashFlowPlanApi {
    @Resource
    private ProjReviewCashFlowPlanApplicationService projReviewCashFlowPlanApplicationService;

    @Override
    public R<String> download() {
        return projReviewCashFlowPlanApplicationService.download();
    }

    @Override
    public R<Void> upload(MultipartFile file, Long projReviewId) {
        return projReviewCashFlowPlanApplicationService.upload(file, projReviewId);
    }

    @Override
    public R<List<ProjReviewCashFlowPlanListRSP>> list(ProjReviewCashFlowPlanListREQ projReviewCashFlowPlanListREQ) {
        return projReviewCashFlowPlanApplicationService.list(projReviewCashFlowPlanListREQ);
    }

    @Override
    public void exportRent(ProjReviewCashFlowPlanExportREQ projReviewCashFlowPlanExportREQ) {
        projReviewCashFlowPlanApplicationService.exportRent(projReviewCashFlowPlanExportREQ);
    }

    @Override
    public void exportCashFlow(ProjReviewCashFlowPlanExportREQ projReviewCashFlowPlanExportREQ) {
        projReviewCashFlowPlanApplicationService.exportCashFlow(projReviewCashFlowPlanExportREQ);
    }

    @Override
    public R<Void> generate(SinglePkREQ singlePkREQ) {
        return projReviewCashFlowPlanApplicationService.generate(singlePkREQ);
    }

    @Override
    public R<IRRCalculateResultRSP> calculateIRR(SinglePkREQ singlePkREQ) {
        return projReviewCashFlowPlanApplicationService.calculateIRR(singlePkREQ);
    }
}
