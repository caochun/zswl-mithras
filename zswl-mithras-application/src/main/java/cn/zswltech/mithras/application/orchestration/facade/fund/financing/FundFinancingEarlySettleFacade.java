package cn.zswltech.mithras.application.orchestration.facade.fund.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingEarlySettleApplicationService;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanRSP;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanSaveREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.fund.application.auth.financing.FundFinancingMainModifyAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingEarlySettlePlanService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/2/23
 * @description
 */
@Service
public class FundFinancingEarlySettleFacade implements FundFinancingEarlySettleApplicationService {
    @Resource
    private FundFinancingEarlySettlePlanService financingEarlySettlePlanService;

    @Override
    public R<FundFinancingEarlySettlePlanRSP> getEarlySettlePlan(@Valid SingleFinancingIdREQ req) {
        return R.ok(financingEarlySettlePlanService.getEarlySettlePlan(req));
    }

    @DataAuthCheck(keyFieldName = "financingId", checkerClass = FundFinancingMainModifyAuthChecker.class, businessModule = "FUND_FINANCING")
    @Override
    public R<Void> saveEarlySettlePlan(@Valid FundFinancingEarlySettlePlanSaveREQ req) {
        financingEarlySettlePlanService.saveEarlySettlePlan(req);
        return R.ok();
    }
}
