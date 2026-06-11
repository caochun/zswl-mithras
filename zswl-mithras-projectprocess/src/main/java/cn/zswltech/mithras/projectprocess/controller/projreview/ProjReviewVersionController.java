package cn.zswltech.mithras.projectprocess.controller.projreview;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projreview.ClientMaterialsLackInfoRSP;
import cn.zswltech.mithras.dto.projreview.ProjReviewEffectREQ;
import cn.zswltech.mithras.dto.projreview.ProjReviewRatingCheckRSP;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewCompareREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import java.util.List;
import java.util.Map;
import cn.zswltech.mithras.api.projreview.ProjReviewVersionApi;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjReviewVersionController implements ProjReviewVersionApi {
    @Resource
    private ProjReviewVersionApplicationService projReviewVersionApplicationService;

    @Override
    public R<ClientMaterialsLackInfoRSP> checkClientMaterials(SinglePkREQ req) {
        return projReviewVersionApplicationService.checkClientMaterials(req);
    }

    @Override
    public R<ProjReviewRatingCheckRSP> checkRatingInfo(SinglePkREQ req) {
        return projReviewVersionApplicationService.checkRatingInfo(req);
    }

    @Override
    public R<Void> effect(ProjReviewEffectREQ req) {
        return projReviewVersionApplicationService.effect(req);
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        return projReviewVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(ProjReviewVersionDiffREQ req) {
        return projReviewVersionApplicationService.comparePreVersion(req);
    }

    @Override
    public R<Map<String, DiffValue>> projReviewPricingBaseInfoCompare(ProjReviewCompareREQ req) {
        return projReviewVersionApplicationService.projReviewPricingBaseInfoCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> projReviewPricingPriceCompare(ProjReviewCompareREQ req) {
        return projReviewVersionApplicationService.projReviewPricingPriceCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> projReviewPricingCashFlowPlanCompare(ProjReviewCompareREQ req) {
        return projReviewVersionApplicationService.projReviewPricingCashFlowPlanCompare(req);
    }
}
