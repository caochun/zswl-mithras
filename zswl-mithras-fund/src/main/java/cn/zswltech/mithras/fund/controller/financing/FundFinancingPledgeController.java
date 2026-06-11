package cn.zswltech.mithras.fund.controller.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingPledgeApi;
import cn.zswltech.mithras.dto.fund.financing.pledge.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingPledgeApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingPledgeController implements FundFinancingPledgeApi {
    @Resource
    private FundFinancingPledgeApplicationService fundFinancingPledgeApplicationService;

    @Override
    public R<List<FundFinancingPledgeProjListRSP>> projList(@Valid FundFinancingPledgeProjListREQ req) {
        return fundFinancingPledgeApplicationService.projList(req);
    }

    @Override
    public R<List<FundFinancingPledgeContractListRSP>> contractList(@Valid FundFinancingPledgeContractListREQ req) {
        return fundFinancingPledgeApplicationService.contractList(req);
    }

    @Override
    public R<Void> create(@Valid FundFinancingPledgeCreateREQ req) {
        return fundFinancingPledgeApplicationService.create(req);
    }

    @Override
    public R<Void> modify(@Valid FundFinancingPledgeModifyREQ req) {
        return fundFinancingPledgeApplicationService.modify(req);
    }

    @Override
    public R<List<FundFinancingPledgeListRSP>> list(@Valid FundFinancingPledgeListREQ req) {
        return fundFinancingPledgeApplicationService.list(req);
    }

    @Override
    public R<FundFinancingPledgeDetailRSP> detail(@Valid FundFinancingPledgeDetailREQ req) {
        return fundFinancingPledgeApplicationService.detail(req);
    }

    @Override
    public R<Void> delete(@Valid FundFinancingPledgeDetailREQ req) {
        return fundFinancingPledgeApplicationService.delete(req);
    }

    @Override
    public R<List<FundFinancingContractInfoListRSP>> contractSearch(FundFinancingContractInfoListREQ req) {
        return fundFinancingPledgeApplicationService.contractSearch(req);
    }
}
