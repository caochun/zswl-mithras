package cn.zswltech.mithras.projectprocess.controller.projpricing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingIRRSaveREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceModifyREQ;
import javax.validation.Valid;
import cn.zswltech.mithras.api.projpricing.ProjPricingPriceApi;
import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingPriceApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class ProjPricingPriceController implements ProjPricingPriceApi {
    @Resource
    private ProjPricingPriceApplicationService projPricingPriceApplicationService;

    @Override
    public R<Void> modify(ProjPricingPriceModifyREQ req) {
        return projPricingPriceApplicationService.modify(req);
    }

    @Override
    public R<ProjPricingPriceDetailRSP> detail(ProjPricingPriceDetailREQ req) {
        return projPricingPriceApplicationService.detail(req);
    }

    @Override
    public R<Void> saveIrrPercent(ProjPricingIRRSaveREQ req) {
        return projPricingPriceApplicationService.saveIrrPercent(req);
    }
}
