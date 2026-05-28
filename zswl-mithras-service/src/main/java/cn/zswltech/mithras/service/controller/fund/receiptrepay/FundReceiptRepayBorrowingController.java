package cn.zswltech.mithras.service.controller.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayBorrowingApi;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingModifyREQ;
import cn.zswltech.mithras.service.auth.checker.fund.FundReceiptRepayModifySubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.fund.receiptrepay.FundReceiptRepayBorrowingMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBorrowing;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBorrowingService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 借款流入
 * @date 2023-02-20
 */
@RestController
public class FundReceiptRepayBorrowingController implements FundReceiptRepayBorrowingApi {

    @Resource
    private FundReceiptRepayBorrowingService fundReceiptRepayBorrowingService;
    @Resource
    private FundReceiptRepayModifySubAuthChecker fundReceiptRepayModifySubAuthChecker;

    @Override
    public R<Void> modify(List<FundReceiptRepayBorrowingModifyREQ> req) {
        fundReceiptRepayModifySubAuthChecker.checkBatch(BusinessModuleEnum.FUND_RECEIPT_REPAY, FundReceiptRepayBorrowingMapper.class, req.stream().map(FundReceiptRepayBorrowingModifyREQ::getId).collect(Collectors.toList()), null);
        fundReceiptRepayBorrowingService.modifyBatch(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundReceiptRepayBorrowingListRSP>> list(FundReceiptRepayBorrowingListREQ req) {
        return R.ok(fundReceiptRepayBorrowingService.list(req));
    }

}