package cn.zswltech.mithras.fund.interfaces.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingApi;
import cn.zswltech.mithras.dto.fund.financing.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingController implements FundFinancingApi {
    @Resource
    private FundFinancingApplicationService fundFinancingApplicationService;

    @Override
    public R<FundFinancingListRSP> pageList(@Valid FundFinancingListREQ req) {
        return fundFinancingApplicationService.pageList(req);
    }

    @Override
    public R<Long> create(@Valid FundFinancingCreateREQ req) {
        return fundFinancingApplicationService.create(req);
    }

    @Override
    public R<Void> close(@Valid SingleFinancingIdREQ req) {
        return fundFinancingApplicationService.close(req);
    }

    @Override
    public R<FundFinancingChangePreCheckRSP> changePreCheck(@Valid SingleFinancingIdREQ req) {
        return fundFinancingApplicationService.changePreCheck(req);
    }

    @Override
    public R<Void> delete(@Valid SingleFinancingIdREQ req) {
        return fundFinancingApplicationService.delete(req);
    }
}
