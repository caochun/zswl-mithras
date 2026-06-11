package cn.zswltech.mithras.fund.directfinancing.controller.directfinancing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingCollectAccountApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.fund.directfinancing.application.directfinancing.FundDirectFinancingCollectAccountApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FundDirectFinancingCollectAccountController implements FundDirectFinancingCollectAccountApi {

    @Resource
    private FundDirectFinancingCollectAccountApplicationService fundDirectFinancingCollectAccountApplicationService;

    @Override
    public R<Void> add(FundDirectFinancingCollectAccountAddREQ req) {
        return fundDirectFinancingCollectAccountApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundDirectFinancingCollectAccountModifyREQ req) {
        return fundDirectFinancingCollectAccountApplicationService.modify(req);
    }

    @Override
    public R<List<FundDirectFinancingCollectAccountListRSP>> list(FundDirectFinancingCollectAccountListREQ req) {
        return fundDirectFinancingCollectAccountApplicationService.list(req);
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingCollectAccountApplicationService.remove(req);
    }
}
