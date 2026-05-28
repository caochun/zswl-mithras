package cn.zswltech.mithras.service.controller.projestablish;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.projestablish.ProjEstablishPriceApi;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceModifyREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.rule.special.ProjEstablishAuthViewRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishPriceService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-07-19
 */
@RestController
public class ProjEstablishPriceController implements ProjEstablishPriceApi {
    @Resource
    private ProjEstablishPriceService priceService;
    @Resource
    private ProjEstablishAuthViewRule projEstablishAuthViewRule;

    @Override
//    @DataAuthCheck(
//            keyFieldName = "projEstablishId",
//            checkerClass = CommonViewMainAuthCheckerNew.class,
//            businessModule = BusinessModuleEnum.PROJ_ESTABLISH
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
            businessModule = BusinessModuleEnum.PROJ_ESTABLISH
    )
    public R<Void> modify(ProjEstablishPriceModifyREQ req) {
        priceService.modify(req);
        return R.ok();
    }
}