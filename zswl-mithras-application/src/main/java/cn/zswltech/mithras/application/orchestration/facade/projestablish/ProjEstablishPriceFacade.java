package cn.zswltech.mithras.application.orchestration.facade.projestablish;

import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishPriceApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceModifyREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.rule.special.ProjEstablishAuthViewRule;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishPriceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-07-19
 */
@Service
public class ProjEstablishPriceFacade implements ProjEstablishPriceApplicationService {
    @Resource
    private ProjEstablishPriceService priceService;
    @Resource
    private ProjEstablishAuthViewRule projEstablishAuthViewRule;

    @Override
//    @DataAuthCheck(
//            keyFieldName = "projEstablishId",
//            checkerClass = CommonViewMainAuthCheckerNew.class,
//            businessModule = "PROJ_ESTABLISH"
//    )
    public R<ProjEstablishPriceDetailRSP> detail(ProjEstablishPriceDetailREQ req) {
        // 特殊权限校验
        projEstablishAuthViewRule.checkEstablish(req.getProjEstablishId());
        return R.ok(priceService.detail(req.getProjEstablishId()));
    }

    @Override
    @DataAuthCheck(
            keyFieldName = "aocPriceModifyREQ.projEstablishId,factoringPriceModifyREQ.projEstablishId,leasePriceModifyREQ.projEstablishId",
            checkerClass = CommonModifyMainAuthCheckerNew.class,
            businessModule = "PROJ_ESTABLISH"
    )
    public R<Void> modify(ProjEstablishPriceModifyREQ req) {
        priceService.modify(req);
        return R.ok();
    }
}