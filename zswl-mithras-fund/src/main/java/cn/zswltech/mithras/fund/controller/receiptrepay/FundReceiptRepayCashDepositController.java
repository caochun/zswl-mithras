package cn.zswltech.mithras.fund.controller.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayCashDepositApi;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositModifyREQ;
import javax.annotation.Resource;
import java.util.List;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptRepayCashDepositApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundReceiptRepayCashDepositController implements FundReceiptRepayCashDepositApi {
    @Resource
    private FundReceiptRepayCashDepositApplicationService fundReceiptRepayCashDepositApplicationService;

    @Override
    public R<Void> modify(List<FundReceiptRepayCashDepositModifyREQ> req) {
        return fundReceiptRepayCashDepositApplicationService.modify(req);
    }

    @Override
    public R<PageR<FundReceiptRepayCashDepositListRSP>> list(FundReceiptRepayCashDepositListREQ req) {
        return fundReceiptRepayCashDepositApplicationService.list(req);
    }
}
