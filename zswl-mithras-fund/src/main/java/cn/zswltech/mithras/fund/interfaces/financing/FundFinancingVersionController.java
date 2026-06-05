package cn.zswltech.mithras.fund.interfaces.financing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingVersionApi;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingEffectREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingSubmitREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingLibVersionService;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingVersionController implements FundFinancingVersionApi {
    @Resource
    private FundFinancingVersionApplicationService fundFinancingVersionApplicationService;

    @Override
    public R<String> effect(@Valid FundFinancingSubmitREQ req) {
        return fundFinancingVersionApplicationService.effect(req);
    }

    @Override
    public R<Void> carryInterest(@Valid FundFinancingEffectREQ req) {
        return fundFinancingVersionApplicationService.carryInterest(req);
    }

    @Override
    public R<Void> confirmChangeLpr(@Valid SingleFinancingIdREQ req) {
        return fundFinancingVersionApplicationService.confirmChangeLpr(req);
    }

    @Override
    public R<String> submitEarlySettle(@Valid SingleFinancingIdREQ req) {
        return fundFinancingVersionApplicationService.submitEarlySettle(req);
    }

    @Override
    public R<Void> cancelChange(@Valid SingleFinancingIdREQ req) {
        return fundFinancingVersionApplicationService.cancelChange(req);
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(@Valid CommonVersionListREQ req) {
        return fundFinancingVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(@Valid FundFinancingVersionDiffREQ req) {
        return fundFinancingVersionApplicationService.comparePreVersion(req);
    }
}
