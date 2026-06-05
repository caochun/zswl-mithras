package cn.zswltech.mithras.service.application.projreview;

import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewPriceApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewIRRSaveREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceModifyREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author zhaozhengkang
 * @description:
 * @since:
 */
@Service
@Slf4j
public class ProjReviewPriceFacade implements ProjReviewPriceApplicationService {

    @Resource
    private ProjReviewPriceService priceService;

    @Override
    @DataAuthCheck(
            keyFieldName = "aocPriceModifyREQ.projectId,factoringPriceModifyREQ.projectId,leasePriceModifyREQ.projectId",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.PROJ_REVIEW
    )
    public R<Void> modify(ProjReviewPriceModifyREQ req) {
        priceService.modify(req);
        return R.ok();
    }
    @Override
    @DataAuthCheck(
            keyFieldName = "id",
            checkerClass = CommonViewMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.PROJ_REVIEW
    )
    public R<ProjReviewPriceDetailRSP> detail(ProjReviewPriceDetailREQ req) {
        return R.ok(priceService.detail(req.getId()));
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "projReviewId",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = BusinessModuleEnum.PROJ_REVIEW
    )
    public R<Void> saveIrrPercent(@Valid ProjReviewIRRSaveREQ req) {
        priceService.saveIrr(req.getProjReviewId(), req.getIrrPercent());
        return R.ok();
    }
}
