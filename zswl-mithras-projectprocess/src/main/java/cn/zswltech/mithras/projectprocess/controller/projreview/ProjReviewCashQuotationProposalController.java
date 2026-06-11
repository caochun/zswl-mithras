package cn.zswltech.mithras.projectprocess.controller.projreview;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowMeetMinutePlanExportREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowMeetMinutePlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import cn.zswltech.mithras.api.projreview.ProjReviewCashQuotationProposalApi;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewCashQuotationProposalApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjReviewCashQuotationProposalController implements ProjReviewCashQuotationProposalApi {
    @Resource
    private ProjReviewCashQuotationProposalApplicationService projReviewCashQuotationProposalApplicationService;

    @Override
    public R<Void> upload(MultipartFile file, Long meetMinuteId) {
        return projReviewCashQuotationProposalApplicationService.upload(file, meetMinuteId);
    }

    @Override
    public R<List<ProjReviewCashFlowPlanListRSP>> list(ProjReviewCashFlowMeetMinutePlanListREQ projReviewCashFlowPlanListREQ) {
        return projReviewCashQuotationProposalApplicationService.list(projReviewCashFlowPlanListREQ);
    }

    @Override
    public void exportRent(ProjReviewCashFlowMeetMinutePlanExportREQ projReviewCashFlowPlanExportREQ) {
        projReviewCashQuotationProposalApplicationService.exportRent(projReviewCashFlowPlanExportREQ);
    }

    @Override
    public void exportCashFlow(ProjReviewCashFlowMeetMinutePlanExportREQ projReviewCashFlowPlanExportREQ) {
        projReviewCashQuotationProposalApplicationService.exportCashFlow(projReviewCashFlowPlanExportREQ);
    }
}
