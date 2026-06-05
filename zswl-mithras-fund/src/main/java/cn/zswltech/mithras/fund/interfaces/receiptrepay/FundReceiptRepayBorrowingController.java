package cn.zswltech.mithras.fund.interfaces.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayBorrowingApi;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingModifyREQ;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptRepayBorrowingApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundReceiptRepayBorrowingController implements FundReceiptRepayBorrowingApi {
    @Resource
    private FundReceiptRepayBorrowingApplicationService fundReceiptRepayBorrowingApplicationService;

    @Override
    public R<Void> modify(List<FundReceiptRepayBorrowingModifyREQ> req) {
        return fundReceiptRepayBorrowingApplicationService.modify(req);
    }

    @Override
    public R<PageR<FundReceiptRepayBorrowingListRSP>> list(FundReceiptRepayBorrowingListREQ req) {
        return fundReceiptRepayBorrowingApplicationService.list(req);
    }
}
