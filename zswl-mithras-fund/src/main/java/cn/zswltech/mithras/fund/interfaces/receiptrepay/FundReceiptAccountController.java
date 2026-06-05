package cn.zswltech.mithras.fund.interfaces.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptAccountApi;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import javax.annotation.Resource;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptAccountApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundReceiptAccountController implements FundReceiptAccountApi {
    @Resource
    private FundReceiptAccountApplicationService fundReceiptAccountApplicationService;

    @Override
    public R<Void> add(FundReceiptAccountAddREQ req) {
        return fundReceiptAccountApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FundReceiptAccountModifyREQ req) {
        return fundReceiptAccountApplicationService.modify(req);
    }

    @Override
    public R<PageR<FundReceiptAccountListRSP>> list(FundReceiptAccountListREQ req) {
        return fundReceiptAccountApplicationService.list(req);
    }

    @Override
    public R<Void> remove(FundReceiptAccountRemoveREQ req) {
        return fundReceiptAccountApplicationService.remove(req);
    }
}
