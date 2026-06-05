package cn.zswltech.mithras.service.application.projpricing;

import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingPriceApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingIRRSaveREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceModifyREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingPriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
public class ProjPricingPriceFacade implements ProjPricingPriceApplicationService{

    @Resource
    private ProjPricingPriceService priceService;

    @Override
    @DataAuthCheck(
            keyFieldName = "aocPriceModifyREQ.projectId,factoringPriceModifyREQ.projectId,leasePriceModifyREQ.projectId",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.PROJ_PRICING
    )
    public R<Void> modify(ProjPricingPriceModifyREQ req) {
        priceService.modify(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonViewMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.PROJ_PRICING
    )
    public R<ProjPricingPriceDetailRSP> detail(ProjPricingPriceDetailREQ req) {
        return R.ok(priceService.detail(req.getId()));
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "projPricingId",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.PROJ_PRICING
    )
    public R<Void> saveIrrPercent(ProjPricingIRRSaveREQ req) {
        priceService.saveIrr(req.getProjPricingId(), req.getIrrPercent());
        return R.ok();
    }
}
