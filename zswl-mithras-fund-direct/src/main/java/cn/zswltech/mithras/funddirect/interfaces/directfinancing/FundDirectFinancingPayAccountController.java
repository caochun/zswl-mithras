package cn.zswltech.mithras.funddirect.interfaces.directfinancing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.directfinancing.FundDirectFinancingPayAccountApi;
import cn.zswltech.mithras.dto.fund.directfinancing.*;
import cn.zswltech.mithras.funddirect.application.directfinancing.FundDirectFinancingPayAccountApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class FundDirectFinancingPayAccountController implements FundDirectFinancingPayAccountApi {

    @Resource
    private FundDirectFinancingPayAccountApplicationService fundDirectFinancingPayAccountApplicationService;

    @Override
    public R<Void> add(FundDirectFinancingPayAccountAddREQ req) {
        return fundDirectFinancingPayAccountApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundDirectFinancingPayAccountModifyREQ req) {
        return fundDirectFinancingPayAccountApplicationService.modify(req);
    }

    @Override
    public R<List<FundDirectFinancingPayAccountListRSP>> list(FundDirectFinancingPayAccountListREQ req) {
        return fundDirectFinancingPayAccountApplicationService.list(req);
    }

    @Override
    public R<Void> remove(FundDirectFinancingSingleIdREQ req) {
        return fundDirectFinancingPayAccountApplicationService.remove(req);
    }
}
