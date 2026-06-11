package cn.zswltech.mithras.fund.controller.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayCashFlowApi;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowModifyREQ;
import javax.annotation.Resource;
import java.util.List;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptRepayCashFlowApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundReceiptRepayCashFlowController implements FundReceiptRepayCashFlowApi {
    @Resource
    private FundReceiptRepayCashFlowApplicationService fundReceiptRepayCashFlowApplicationService;

    @Override
    public R<Void> modify(List<FundReceiptRepayCashFlowModifyREQ> req) {
        return fundReceiptRepayCashFlowApplicationService.modify(req);
    }

    @Override
    public R<PageR<FundReceiptRepayCashFlowListRSP>> list(FundReceiptRepayCashFlowListREQ req) {
        return fundReceiptRepayCashFlowApplicationService.list(req);
    }
}
