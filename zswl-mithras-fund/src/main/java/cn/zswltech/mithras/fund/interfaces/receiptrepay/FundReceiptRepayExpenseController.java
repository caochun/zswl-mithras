package cn.zswltech.mithras.fund.interfaces.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayExpenseApi;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseModifyREQ;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptRepayExpenseApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundReceiptRepayExpenseController implements FundReceiptRepayExpenseApi {
    @Resource
    private FundReceiptRepayExpenseApplicationService fundReceiptRepayExpenseApplicationService;

    @Override
    public R<Void> modify(List<FundReceiptRepayExpenseModifyREQ> req) {
        return fundReceiptRepayExpenseApplicationService.modify(req);
    }

    @Override
    public R<PageR<FundReceiptRepayExpenseListRSP>> list(FundReceiptRepayExpenseListREQ req) {
        return fundReceiptRepayExpenseApplicationService.list(req);
    }
}
