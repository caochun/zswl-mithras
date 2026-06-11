package cn.zswltech.mithras.application.orchestration.facade.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptRepayExpenseApplicationService;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseModifyREQ;
import cn.zswltech.mithras.fund.application.auth.receiptrepay.FundReceiptRepayModifySubAuthChecker;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayExpenseMapper;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayExpenseService;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 费用一览表
 * @date 2023-02-20
 */
@Service
public class FundReceiptRepayExpenseFacade implements FundReceiptRepayExpenseApplicationService {

    @Resource
    private FundReceiptRepayExpenseService fundReceiptRepayExpenseService;
    @Resource
    private FundReceiptRepayModifySubAuthChecker fundReceiptRepayModifySubAuthChecker;

    @Override
    public R<Void> modify(List<FundReceiptRepayExpenseModifyREQ> req) {
        fundReceiptRepayModifySubAuthChecker.checkBatch(BusinessModuleEnum.FUND_RECEIPT_REPAY, FundReceiptRepayExpenseMapper.class, req.stream().map(FundReceiptRepayExpenseModifyREQ::getId).collect(Collectors.toList()), null);
        fundReceiptRepayExpenseService.modifyBatch(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundReceiptRepayExpenseListRSP>> list(FundReceiptRepayExpenseListREQ req) {
        return R.ok(fundReceiptRepayExpenseService.list(req));
    }

}