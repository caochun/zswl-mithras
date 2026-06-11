package cn.zswltech.mithras.fund.controller.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingCollectAccountApi;
import cn.zswltech.mithras.dto.fund.financing.collectaccount.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingCollectAccountApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingCollectAccountController implements FundFinancingCollectAccountApi {
    @Resource
    private FundFinancingCollectAccountApplicationService fundFinancingCollectAccountApplicationService;

    @Override
    public R<Void> create(@Valid FundFinancingCollectAccountCreateREQ req) {
        return fundFinancingCollectAccountApplicationService.create(req);
    }

    @Override
    public R<Void> modify(@Valid FundFinancingCollectAccountModifyREQ req) {
        return fundFinancingCollectAccountApplicationService.modify(req);
    }

    @Override
    public R<List<FundFinancingCollectAccountListRSP>> list(@Valid FundFinancingCollectAccountListREQ req) {
        return fundFinancingCollectAccountApplicationService.list(req);
    }

    @Override
    public R<Void> delete(@Valid FundFinancingCollectAccountDetailREQ req) {
        return fundFinancingCollectAccountApplicationService.delete(req);
    }
}
