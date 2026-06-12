package cn.zswltech.mithras.application.orchestration.facade.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingPlanApplicationService;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.plan.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.application.auth.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.fund.application.auth.financing.FundFinancingSubModifyAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingPlanMapper;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Service
public class FundFinancingPlanFacade implements FundFinancingPlanApplicationService {
    @Resource
    private FundFinancingPlanService financingPlanService;

    @DataAuthCheck(keyFieldName = "id", checkerClass = FundFinancingSubModifyAuthChecker.class, businessModule = "FUND_FINANCING", mapperClass = FundFinancingPlanMapper.class)
    @Override
    public R<Void> modify(@Valid FundFinancingPlanModifyREQ req) {
        financingPlanService.modify(req);
        return R.ok();
    }

    @Override
    public R<FundFinancingPlanDetailRSP> detail(@Valid SingleFinancingIdREQ req) {
        return R.ok(financingPlanService.detail(req));
    }

    @Override
    public R<FundFinancingChangeLprRSP> getLprData(@Valid SingleFinancingIdREQ req) {
        return R.ok(financingPlanService.getLprData(req));
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> changeLpr(@Valid FundFinancingChangeLprREQ req) {
        financingPlanService.changeLpr(req);
        return R.ok();
    }

    @Override
    public R<Long> calculateGuaranteeAmount(@Valid FundFinancingCalcGuaranteeAmountREQ req) {
        return R.ok(financingPlanService.calculateGuaranteeAmount(req.getFinancingAmount(), req.getGuaranteeRate(), req.getFinancingMonth()));
    }
}
