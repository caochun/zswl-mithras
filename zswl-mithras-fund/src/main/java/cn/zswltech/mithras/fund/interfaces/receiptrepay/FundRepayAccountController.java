package cn.zswltech.mithras.fund.interfaces.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundRepayAccountApi;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import javax.annotation.Resource;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundRepayAccountApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundRepayAccountController implements FundRepayAccountApi {
    @Resource
    private FundRepayAccountApplicationService fundRepayAccountApplicationService;

    @Override
    public R<Void> add(FundRepayAccountAddREQ req) {
        return fundRepayAccountApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundRepayAccountModifyREQ req) {
        return fundRepayAccountApplicationService.modify(req);
    }

    @Override
    public R<PageR<FundRepayAccountListRSP>> list(FundRepayAccountListREQ req) {
        return fundRepayAccountApplicationService.list(req);
    }

    @Override
    public R<Void> remove(FundRepayAccountRemoveREQ req) {
        return fundRepayAccountApplicationService.remove(req);
    }
}
