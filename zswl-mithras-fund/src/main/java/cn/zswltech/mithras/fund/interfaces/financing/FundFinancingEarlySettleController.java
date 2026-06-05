package cn.zswltech.mithras.fund.interfaces.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingEarlySettleApi;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanRSP;
import cn.zswltech.mithras.dto.fund.financing.earlysettle.FundFinancingEarlySettlePlanSaveREQ;
import javax.annotation.Resource;
import javax.validation.Valid;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingEarlySettleApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingEarlySettleController implements FundFinancingEarlySettleApi {
    @Resource
    private FundFinancingEarlySettleApplicationService fundFinancingEarlySettleApplicationService;

    @Override
    public R<FundFinancingEarlySettlePlanRSP> getEarlySettlePlan(@Valid SingleFinancingIdREQ req) {
        return fundFinancingEarlySettleApplicationService.getEarlySettlePlan(req);
    }

    @Override
    public R<Void> saveEarlySettlePlan(@Valid FundFinancingEarlySettlePlanSaveREQ req) {
        return fundFinancingEarlySettleApplicationService.saveEarlySettlePlan(req);
    }
}
