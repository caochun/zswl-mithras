package cn.zswltech.mithras.projectprocess.controller.projreview;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewIRRSaveREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceModifyREQ;
import cn.zswltech.mithras.api.projreview.ProjReviewPriceApi;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewPriceApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjReviewPriceController implements ProjReviewPriceApi {
    @Resource
    private ProjReviewPriceApplicationService projReviewPriceApplicationService;

    @Override
    public R<Void> modify(ProjReviewPriceModifyREQ req) {
        return projReviewPriceApplicationService.modify(req);
    }

    @Override
    public R<ProjReviewPriceDetailRSP> detail(ProjReviewPriceDetailREQ req) {
        return projReviewPriceApplicationService.detail(req);
    }

    @Override
    public R<Void> saveIrrPercent(ProjReviewIRRSaveREQ req) {
        return projReviewPriceApplicationService.saveIrrPercent(req);
    }
}
