package cn.zswltech.mithras.projectprocess.controller.projpricing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanExportREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListCompareRSP;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.api.projpricing.ProjPricingCashFlowPlanApi;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingCashFlowPlanApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjPricingCashFlowPlanController implements ProjPricingCashFlowPlanApi {
    @Resource
    private ProjPricingCashFlowPlanApplicationService projPricingCashFlowPlanApplicationService;

    @Override
    public R<String> download() {
        return projPricingCashFlowPlanApplicationService.download();
    }

    @Override
    public R<Void> upload(MultipartFile file, Long projPricingId) {
        return projPricingCashFlowPlanApplicationService.upload(file, projPricingId);
    }

    @Override
    public R<List<ProjPricingCashFlowPlanListRSP>> list(ProjPricingCashFlowPlanListREQ projPricingCashFlowPlanListREQ) {
        return projPricingCashFlowPlanApplicationService.list(projPricingCashFlowPlanListREQ);
    }

    @Override
    public void exportRent(ProjPricingCashFlowPlanExportREQ projPricingCashFlowPlanExportREQ) {
        projPricingCashFlowPlanApplicationService.exportRent(projPricingCashFlowPlanExportREQ);
    }

    @Override
    public void exportCashFlow(ProjPricingCashFlowPlanExportREQ projPricingCashFlowPlanExportREQ) {
        projPricingCashFlowPlanApplicationService.exportCashFlow(projPricingCashFlowPlanExportREQ);
    }

    @Override
    public R<Void> generate(SinglePkREQ singlePkREQ) {
        return projPricingCashFlowPlanApplicationService.generate(singlePkREQ);
    }

    @Override
    public R<IRRCalculateResultRSP> calculateIRR(SinglePkREQ singlePkREQ) {
        return projPricingCashFlowPlanApplicationService.calculateIRR(singlePkREQ);
    }

    @Override
    public R<List<ProjPricingCashFlowPlanListCompareRSP>> compare(ProjPricingCashFlowPlanListREQ projPricingCashFlowPlanListREQ) {
        return projPricingCashFlowPlanApplicationService.compare(projPricingCashFlowPlanListREQ);
    }
}
