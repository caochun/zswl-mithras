package cn.zswltech.mithras.application.orchestration.facade.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptRepayBorrowingApplicationService;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingModifyREQ;
import cn.zswltech.mithras.fund.application.auth.receiptrepay.FundReceiptRepayModifySubAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.persistence.mapper.receiptrepay.FundReceiptRepayBorrowingMapper;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBorrowing;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBorrowingService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 借款流入
 * @date 2023-02-20
 */
@Service
public class FundReceiptRepayBorrowingFacade implements FundReceiptRepayBorrowingApplicationService {

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