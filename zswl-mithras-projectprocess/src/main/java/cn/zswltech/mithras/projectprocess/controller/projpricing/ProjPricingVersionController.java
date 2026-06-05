package cn.zswltech.mithras.projectprocess.controller.projpricing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.datacompare.ProjReviewPriceCompareRSP;
import cn.zswltech.mithras.dto.projpricing.ProjPricingEffectREQ;
import cn.zswltech.mithras.dto.projpricing.ProjPricingVersionDiffREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingCompareREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import cn.zswltech.mithras.api.projpricing.ProjPricingVersionApi;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjPricingVersionController implements ProjPricingVersionApi {
    @Resource
    private ProjPricingVersionApplicationService projPricingVersionApplicationService;

    @Override
    public R<Void> effect(ProjPricingEffectREQ req) {
        return projPricingVersionApplicationService.effect(req);
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        return projPricingVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(ProjPricingVersionDiffREQ req) {
        return projPricingVersionApplicationService.comparePreVersion(req);
    }

    @Override
    public R<Map<String, DiffValue>> projPricingReviewBaseInfoCompare(ProjPricingCompareREQ req) {
        return projPricingVersionApplicationService.projPricingReviewBaseInfoCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> projPricingReviewPriceCompare(ProjPricingCompareREQ req) {
        return projPricingVersionApplicationService.projPricingReviewPriceCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> projPricingReviewCashFlowPlanCompare(ProjPricingCompareREQ req) {
        return projPricingVersionApplicationService.projPricingReviewCashFlowPlanCompare(req);
    }
}
