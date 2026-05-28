package cn.zswltech.mithras.service.controller.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayCashDepositApi;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositModifyREQ;
import cn.zswltech.mithras.service.auth.checker.fund.FundReceiptRepayModifySubAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.fund.receiptrepay.FundReceiptRepayCashDepositMapper;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashDepositService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 保证金明细
 * @date 2023-02-20
 */
@RestController
public class FundReceiptRepayCashDepositController implements FundReceiptRepayCashDepositApi {

    @Resource
    private FundReceiptRepayCashDepositService fundReceiptRepayCashDepositService;
    @Resource
    private FundReceiptRepayModifySubAuthChecker fundReceiptRepayModifySubAuthChecker;


    @Override
    public R<Void> modify(List<FundReceiptRepayCashDepositModifyREQ> req) {
        fundReceiptRepayModifySubAuthChecker.checkBatch(BusinessModuleEnum.FUND_RECEIPT_REPAY, FundReceiptRepayCashDepositMapper.class, req.stream().map(FundReceiptRepayCashDepositModifyREQ::getId).collect(Collectors.toList()), null);
        fundReceiptRepayCashDepositService.modifyBatch(req);
        return R.ok();
    }

    @Override
    public R<PageR<FundReceiptRepayCashDepositListRSP>> list(FundReceiptRepayCashDepositListREQ req) {
        return R.ok(fundReceiptRepayCashDepositService.list(req));
    }

}