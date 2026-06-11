package cn.zswltech.mithras.fund.controller.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingPlanApi;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.plan.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingPlanApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingPlanController implements FundFinancingPlanApi {
    @Resource
    private FundFinancingPlanApplicationService fundFinancingPlanApplicationService;

    @Override
    public R<Void> modify(@Valid FundFinancingPlanModifyREQ req) {
        return fundFinancingPlanApplicationService.modify(req);
    }

    @Override
    public R<FundFinancingPlanDetailRSP> detail(@Valid SingleFinancingIdREQ req) {
        return fundFinancingPlanApplicationService.detail(req);
    }

    @Override
    public R<FundFinancingChangeLprRSP> getLprData(@Valid SingleFinancingIdREQ req) {
        return fundFinancingPlanApplicationService.getLprData(req);
    }

    @Override
    public R<Void> changeLpr(@Valid FundFinancingChangeLprREQ req) {
        return fundFinancingPlanApplicationService.changeLpr(req);
    }

    @Override
    public R<Long> calculateGuaranteeAmount(@Valid FundFinancingCalcGuaranteeAmountREQ req) {
        return fundFinancingPlanApplicationService.calculateGuaranteeAmount(req);
    }
}
