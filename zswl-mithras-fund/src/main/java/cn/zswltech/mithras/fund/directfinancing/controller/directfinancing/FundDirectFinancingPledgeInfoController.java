package cn.zswltech.mithras.fund.directfinancing.controller.directfinancing;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingPledgeInfoApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.fund.directfinancing.application.directfinancing.FundDirectFinancingPledgeInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class FundDirectFinancingPledgeInfoController implements FundDirectFinancingPledgeInfoApi {

    @Resource
    private FundDirectFinancingPledgeInfoApplicationService fundDirectFinancingPledgeInfoApplicationService;

    @Override
    public R<Void> add(FundDirectFinancingPledgeInfoAddREQ req) {
        return fundDirectFinancingPledgeInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundDirectFinancingPledgeInfoModifyREQ req) {
        return fundDirectFinancingPledgeInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<FundDirectFinancingPledgeInfoListRSP>> list(FundDirectFinancingPledgeInfoListREQ req) {
        return fundDirectFinancingPledgeInfoApplicationService.list(req);
    }

    @Override
    public R<FundDirectFinancingPledgeInfoDetailRSP> detail(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingPledgeInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingPledgeInfoApplicationService.remove(req);
    }

    @Override
    public void exportExcel(FundDirectFinancingPledgeInfoListREQ req) {
        fundDirectFinancingPledgeInfoApplicationService.exportExcel(req);
    }
}
